package com.bigyj.client.sender;

import com.bigyj.client.client.ClientSession;
import com.bigyj.entity.Msg;
import com.bigyj.entity.User;
import lombok.Data;

@Data
public abstract class MsgSender {
    private User user;
    private ClientSession session;
    public abstract void sendMsg(Msg msg) ;
}
