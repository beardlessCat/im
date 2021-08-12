package com.bigyj.client.sender;

import com.bigyj.client.client.ClientSession;
import com.bigyj.entity.Msg;
import org.springframework.stereotype.Service;

@Service
public class ChatMsgSender extends MsgSender{
    @Override
    public void sendMsg(Msg msg) {
        ClientSession session = getSession();
        session.getChannel().writeAndFlush(msg+"\r\n");
    }
}
