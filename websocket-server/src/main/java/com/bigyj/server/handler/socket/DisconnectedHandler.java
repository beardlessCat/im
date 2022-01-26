package com.bigyj.server.handler.socket;

import com.bigyj.server.cach.SessionCacheSupport;
import com.bigyj.server.holder.LocalSessionHolder;
import com.bigyj.server.session.LocalSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 客户端退出处理逻辑
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class DisconnectedHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private SessionCacheSupport sessionCacheSupport;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.clientDisconnected(ctx);
        logger.info("客户端断开连接");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.clientDisconnected(ctx);
        logger.info("客户端异常断开连接");

    }

    /**
     * 客户单主动断开连接或异常断开连接
     * @param ctx
     */
    private void clientDisconnected(ChannelHandlerContext ctx) {
        LocalSession session = LocalSession.getSession(ctx);
        String userId = session.getUserId();
        LocalSessionHolder.removeServerSession(userId);
        //保存channel信息
        LocalSession serverSession = new LocalSession(ctx.channel());
        serverSession.setLogin(false);
        //移除redis缓存新
        sessionCacheSupport.remove(userId);
    }
}
