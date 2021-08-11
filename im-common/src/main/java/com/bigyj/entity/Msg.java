package com.bigyj.entity;

import lombok.Data;

public class Msg {
    private MsgType msgType ;
    private User user;

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public enum MsgType
    {
        LOGIN,//登陆消息
        CHAT,//聊天消息
        LOGOUT// 退出消息
    }
}
