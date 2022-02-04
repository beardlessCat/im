package com.bigyj.server.session;

import com.bigyj.message.ChatRequestMessage;

public interface ServerSession {
	/**
	 * 发送消息
	 * @param chatRequestMessage 消息
	 */
	boolean writeAndFlush(ChatRequestMessage chatRequestMessage );

	/**
	 * 获取sessionId
	 * @return
	 */
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
