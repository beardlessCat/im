package com.bigyj.server.msg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageReceive {
	@Autowired
	private MessageReceiveHandler messageReceiveHandler;
	/**接收消息的方法*/
	public void receiveMessage(String message){
		messageReceiveHandler.messagePush(message);
	}

}
