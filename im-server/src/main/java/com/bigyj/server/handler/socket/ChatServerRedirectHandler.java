package com.bigyj.server.handler.socket;

import com.bigyj.message.ChatResponseMessage;
import com.bigyj.message.GroupRemoteChatRequestMessage;
import com.bigyj.server.holder.LocalSessionHolder;
import com.bigyj.server.session.LocalSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接收到其他服务端转发来的请求，获取所有本地连接进行消息发送
 */
@Slf4j
@ChannelHandler.Sharable
public class ChatServerRedirectHandler extends SimpleChannelInboundHandler<GroupRemoteChatRequestMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, GroupRemoteChatRequestMessage msg) throws Exception {
		//发送给连接到自身服务器的客户端
		ConcurrentHashMap<String, LocalSession> allSession = LocalSessionHolder.getAll();
		Iterator<String> iter = allSession.keySet().iterator();
		while (iter.hasNext()) {
			LocalSession session  = allSession.get(iter.next());
			session.getChannel().writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
		}
	}
}
