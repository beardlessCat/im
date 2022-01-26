package com.bigyj.server.handler.socket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 接收到其他服务端转发来的请求，获取所有本地连接进行消息发送
 */
@Slf4j
@ChannelHandler.Sharable
public class ChatServerRedirectHandler01 extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
