package com.bigyj.entity;

import com.bigyj.user.User;
import lombok.Data;

@Data
public class MsgDto {
    /**
     * 消息类型
     */
    private Msg.MsgType msgType ;
    /**
     * 用户信息
     */
    private User user;
    /**
     * 消息是否发送成功
     */
    private boolean success ;
    /**
     * 消息接收用户id
     */
    private String toUserId ;
    /**
     * 消息内容
     */
    private String content ;

}
