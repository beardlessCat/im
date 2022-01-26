package com.bigyj.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ServerRequestMessage extends Message {
    private String content;
    private String to;
    private String from;

    public ServerRequestMessage() {
    }

    public ServerRequestMessage(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return ChatRequestMessage;
    }
}
