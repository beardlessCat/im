package com.bigyj.server.handler.websocket;

import com.bigyj.server.manager.MemoryUserManager;
import com.bigyj.server.manager.ServerSessionManager;
import com.bigyj.server.session.LocalSession;
import com.bigyj.server.utils.SpringContextUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebsocketServerFinishHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof SecurityServerHandler.SecurityCheckComplete) {
            logger.info("Security check has passed");
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            logger.info("Handshake has completed");
            SecurityServerHandler.SecurityCheckComplete complete = ctx.channel().attr(SecurityServerHandler.SECURITY_CHECK_COMPLETE_ATTRIBUTE_KEY).get();
            //保存channel信息
            LocalSession serverSession = new LocalSession(ctx.channel());
            serverSession.setUser(MemoryUserManager.getUserByName(complete.getUserId()));
            serverSession.bind();
            //连接信息保存至redis数据库
            ServerSessionManager serverSessionManager = SpringContextUtil.getBean(ServerSessionManager.class);
            serverSessionManager.addServerSession(serverSession);
        }
        super.userEventTriggered(ctx, evt);
    }
}
