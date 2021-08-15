package com.bigyj.entity;

import lombok.Data;

@Data
public class Msg {
    /**
     * 消息类型
     */
    private final MsgType msgType ;
    /**
     * 用户信息
     */
    private final User user;
    /**
     * 消息是否发送成功
     */
    private final boolean success ;
    /**
     * 消息接收用户id
     */
    private final String toUserId ;
    /**
     * 消息内容
     */
    private final String content ;
    public Msg(Builder builder){
        this.msgType = builder.msgType;
        this.user = builder.user;
        this.success = builder.success;
        this.toUserId = builder.toUserId;
        this.content = builder.content;
    }
    public static class Builder{
        private MsgType msgType ;
        private User user;
        private boolean success ;
        private String toUserId ;
        private String content ;
        public Builder(MsgType msgType,User user){
            this.msgType = msgType;
            this.user = user ;
        }
        public Builder setSuccess(boolean success){
            this.success = success ;
            return this;
        }
        public Builder setToUserId(String toUserId){
            this.toUserId = toUserId ;
            return this;
        }
        public Builder setContent(String content){
            this.content = content ;
            return this;
        }
    }
    public enum MsgType
    {
        LOGIN_REQUEST,//登陆请求消息
        LOGIN_RESPONSE,//登陆响应消息
        CHAT,//聊天消息
        LOGOUT_REQUEST,// 退出请求消息
        LOGOUT_RESPONSE,// 退出响应消息
        HEART_BEAT,//心跳
    }
}
