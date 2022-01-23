package com.bigyj.server.handler.socket;

import com.bigyj.message.ChatResponseMessage;
import com.bigyj.message.GroupChatRequestMessage;
import com.bigyj.message.GroupRemoteChatRequestMessage;
import com.bigyj.server.holder.LocalSessionHolder;
import com.bigyj.server.holder.ServerPeerSenderHolder;
import com.bigyj.server.server.ServerPeerSender;
import com.bigyj.server.session.LocalSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
@Slf4j
public class GroupMessageSendHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        //发送给连接到自身服务器的客户端
        ConcurrentHashMap<String, LocalSession> allSession = LocalSessionHolder.getAll();
        Iterator<String> iter = allSession.keySet().iterator();
        while (iter.hasNext()) {
            LocalSession session  = allSession.get(iter.next());
            session.getChannel().writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
        //处理群发远程消息
        ConcurrentHashMap<Long, ServerPeerSender> allRemote = ServerPeerSenderHolder.getAll();
        for(Long key : allRemote.keySet()) {
            allRemote.get(key).getChannel().writeAndFlush(new GroupRemoteChatRequestMessage(msg.getFrom(),msg.getContent()));
        }
    }
}
