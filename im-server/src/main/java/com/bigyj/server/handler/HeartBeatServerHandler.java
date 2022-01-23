package com.bigyj.server.handler;

import com.bigyj.server.session.LocalSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
@Slf4j
public class HeartBeatServerHandler extends IdleStateHandler {
    private static final int READ_IDLE_GAP = 5;

    public HeartBeatServerHandler() {
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        logger.warn( "{}秒内未读到数据，关闭连接",READ_IDLE_GAP);
        LocalSession.closeSession(ctx);
    }
}
