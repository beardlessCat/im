package com.bigyj.server.server;

import com.bigyj.entity.User;
import com.bigyj.server.holder.ServerSessionHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Data
@Slf4j
public class ServerSession {
    public static final AttributeKey<String> KEY_USER_ID =
            AttributeKey.valueOf("key_user_id");

    public static final AttributeKey<ServerSession> SESSION_KEY =
            AttributeKey.valueOf("SESSION_KEY");
    //通道
    private Channel channel;
    //用户
    private User user;

    private final String sessionId;
    private boolean isLogin = false;

    public ServerSession(Channel channel) {
        this.channel = channel;
        this.sessionId = buildNewSessionId();
    }
    private static String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }
    //反向导航
    public static ServerSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.attr(ServerSession.SESSION_KEY).get();
    }

    /**
     * ServerSession 绑定会话
     */
    public ServerSession bind() {
        logger.info(" ServerSession 绑定会话 " + channel.remoteAddress());
        channel.attr(ServerSession.SESSION_KEY).set(this);
        ServerSessionHolder.addServerSession(this);
        isLogin = true;
        return this;
    }
}
