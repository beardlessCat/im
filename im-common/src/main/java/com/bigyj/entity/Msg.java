package com.bigyj.entity;

public class Msg {
    private MsgType msgType ;
    private User user;
    private boolean success ;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

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
        LOGIN_REQUEST,//登陆请求消息
        LOGIN_RESPONSE,//登陆响应消息
        CHAT,//聊天消息
        LOGOUT// 退出消息
    }
}
