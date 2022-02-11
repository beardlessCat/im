package com.bigyj.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupRemoteChatRequestMessage extends Message {
    private String content;
    private String from;

    public GroupRemoteChatRequestMessage(String from, String content) {
        this.content = content;
        this.from = from;
    }

    @Override
    public int getMessageType() {
        return GroupRemoteChatRequestMessage;
    }
}
