package com.bigyj.server.handler.socket;

import com.bigyj.message.ServerPeerConnectedMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Slf4j
@Component
public class ServerPeerConnectedHandler extends SimpleChannelInboundHandler<ServerPeerConnectedMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerPeerConnectedMessage msg) throws Exception {
        ctx.pipeline().addLast(new IdleStateHandler(200, 0, 0));
        // ChannelDuplexHandler 可以同时作为入站和出站处理器
        ctx.pipeline().addLast(new ChannelDuplexHandler() {
            // 用来触发特殊事件
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                IdleStateEvent event = (IdleStateEvent) evt;
                // 触发了读空闲事件·
                if (event.state() == IdleState.READER_IDLE) {
                    logger.info("已经 200s 没有读到数据了");
                    ctx.channel().close();
                }
            }
        });
        //增加聊天的handler
        ctx.pipeline().addLast("chat", new ChatServerRedirectHandler());
        //增加退出的handler
        ctx.pipeline().addLast("logout", new DisconnectedHandler());
    }
}
