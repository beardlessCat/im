package com.bigyj.entity;

import com.bigyj.user.User;
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

    public Msg(MsgBuilder msgBuilder){
        this.msgType = msgBuilder.msgType;
        this.user = msgBuilder.user;
        this.success = msgBuilder.success;
        this.toUserId = msgBuilder.toUserId;
        this.content = msgBuilder.content;
    }

    public static MsgBuilder builder(MsgType msgType, User user) {
        return new MsgBuilder(msgType,user);
    }
    public static MsgBuilder builder() {
        return new MsgBuilder();
    }
    public static class MsgBuilder {
        private MsgType msgType ;
        private User user;
        private boolean success ;
        private String toUserId ;
        private String content ;
        public MsgBuilder(MsgType msgType, User user){
            this.msgType = msgType;
            this.user = user ;
        }
        public MsgBuilder(){
        }
        public MsgBuilder setMsgType(MsgType msgType){
            this.msgType = msgType ;
            return this;
        }
        public MsgBuilder setUser(User user){
            this.user = user ;
            return this;
        }
        public MsgBuilder setSuccess(boolean success){
            this.success = success ;
            return this;
        }
        public MsgBuilder setToUserId(String toUserId){
            this.toUserId = toUserId ;
            return this;
        }
        public MsgBuilder setContent(String content){
            this.content = content ;
            return this;
        }
        public Msg build(){
            return new Msg(this);
        }
    }

    public enum MsgType
    {
        LOGIN_REQUEST,//登陆请求消息
        LOGIN_RESPONSE,//登陆响应消息
        CHAT,//聊天消息
        LOGOUT_REQUEST,// 退出请求消息
        LOGOUT_RESPONSE,// 退出响应消息
        HEART_PING,//心跳ping
        HEART_PONG,//心跳pong
        SERVER_NOTICE//服务通知
    }
}
