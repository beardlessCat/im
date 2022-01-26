package com.bigyj.server.session;

import com.bigyj.entity.MsgDto;
import com.bigyj.message.ChatRequestMessage;
import com.bigyj.message.Message;

public interface ServerSession {
	/**
	 * 发送消息
	 * @param pkg 消息
	 */
	boolean writeAndFlush(ChatRequestMessage pkg);

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
