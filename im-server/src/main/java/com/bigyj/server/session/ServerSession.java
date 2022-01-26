package com.bigyj.server.session;

import com.bigyj.message.websocket.WebSocketMessage;

public interface ServerSession {

	boolean writeAndFlush(WebSocketMessage webSocketMessage);

	String getSessionId();

	/**
	 * 是否有效
	 * @return
	 */
	boolean isValid();

	/**
	 * 获获取用户
	 * @return  用户id
	 */
	String getUserId();
}
