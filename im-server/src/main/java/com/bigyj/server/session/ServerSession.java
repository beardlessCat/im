package com.bigyj.server.session;

import com.bigyj.message.ChatRequestMessage;
import com.bigyj.message.GroupChatResponseMessage;

public interface ServerSession {

	boolean writeAndFlush(ChatRequestMessage chatRequestMessage);

	boolean writeGroupMessage(GroupChatResponseMessage groupChatResponseMessage );

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
