package com.bigyj.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
@Data
public abstract class Message implements Serializable {
    /**
     * 根据消息类型字节，获得对应的消息 class
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends Message> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;

    private int messageType;

    public abstract int getMessageType();

    public static final int LoginRequestMessage = 0;
    public static final int LoginResponseMessage = 1;
    public static final int ChatRequestMessage = 2;
    public static final int ChatResponseMessage = 3;
    public static final int GroupChatRequestMessage = 4;
    public static final int GroupChatResponseMessage = 5;
    public static final int PingMessage = 6;
    public static final int PongMessage = 7;
    public static final int ServerPeerConnectedMessage = 8 ;
    public static final int GroupRemoteChatRequestMessage = 9 ;

    /**
     * 请求类型 byte 值
     */
    public static final int RPC_MESSAGE_TYPE_REQUEST = 101;
    /**
     * 响应类型 byte 值
     */
    public static final int  RPC_MESSAGE_TYPE_RESPONSE = 102;

    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    static {

    }
}
