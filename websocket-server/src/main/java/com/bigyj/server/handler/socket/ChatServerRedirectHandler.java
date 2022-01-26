package com.bigyj.server.handler.socket;

import com.bigyj.message.websocket.WebSocketMessage;
import com.bigyj.server.holder.LocalSessionHolder;
import com.bigyj.server.session.LocalSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 接收到其他服务端转发来的请求，获取所有本地连接进行消息发送
 */
@Slf4j
@ChannelHandler.Sharable
public class ChatServerRedirectHandler extends SimpleChannelInboundHandler<WebSocketMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketMessage msg) throws Exception {
		//查询对方客户端
		String to = msg.getTo();
		LocalSession serverSession = LocalSessionHolder.getServerSession(to);
		//发送消息
		serverSession.writeAndFlush(msg);
	}
}
