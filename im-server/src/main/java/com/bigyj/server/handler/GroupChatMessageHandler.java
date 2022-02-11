package com.bigyj.server.handler;

import com.bigyj.message.GroupChatRequestMessage;
import com.bigyj.message.GroupChatResponseMessage;
import com.bigyj.server.holder.LocalSessionHolder;
import com.bigyj.server.session.LocalSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
@Slf4j
public class GroupChatMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        //发送给连接到自身服务器的客户端
        ConcurrentHashMap<String, LocalSession> allSession = LocalSessionHolder.getAll();
        Iterator<String> iter = allSession.keySet().iterator();
        while (iter.hasNext()) {
            LocalSession session  = allSession.get(iter.next());
            session.writeGroupMessage(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
    }
}
