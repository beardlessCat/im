package com.bigyj.server.session;

import com.bigyj.message.ChatResponseMessage;
import com.bigyj.message.websocket.WebSocketMessage;
import com.bigyj.server.holder.LocalSessionHolder;
import com.bigyj.server.manager.MemoryUserManager;
import com.bigyj.user.User;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Data
public class LocalSession extends AbstractServerSession {
	public static final AttributeKey<String> KEY_USER_ID =
			AttributeKey.valueOf("KEY_USER_ID");

	public static final AttributeKey<LocalSession> SESSION_KEY =
			AttributeKey.valueOf("SESSION_KEY");
	//通道
	private Channel channel;
	//用户
	private User user;
	//sessionId
	private final String sessionId;
	//是否登录
	private boolean isLogin = false;

	public LocalSession(Channel channel) {
		this.channel = channel;
		this.sessionId = buildNewSessionId();
	}

	@Override
	public boolean writeAndFlush(WebSocketMessage webSocketMessage) {
		//获取channel，发送消息
		String userId = webSocketMessage.getTo();
		LocalSession localSession = LocalSessionHolder.getServerSession(userId);
		if(localSession== null){
			return false;
		}else {
			localSession.getChannel().writeAndFlush(new TextWebSocketFrame(new Gson().toJson(webSocketMessage)));
			return true;
		}
	}


	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public boolean isValid() {
		return getUser() != null ? true : false;
	}


	@Override
	public String getUserId() {
		return user.getUid();
	}

	private static String buildNewSessionId() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replaceAll("-", "");
	}

	//反向导航
	public static LocalSession getSession(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		return channel.attr(LocalSession.SESSION_KEY).get();
	}

	/**
	 * ServerSession 绑定会话
	 */
	public LocalSession bind() {
		logger.info(" ServerSession 绑定会话 " + channel.remoteAddress());
		channel.attr(LocalSession.SESSION_KEY).set(this);
		LocalSessionHolder.addServerSession(this);
		isLogin = true;
		return this;
	}
}
