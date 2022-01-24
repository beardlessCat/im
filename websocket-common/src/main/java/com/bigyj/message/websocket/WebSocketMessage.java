package com.bigyj.message.websocket;

import com.bigyj.message.Message;
import lombok.Data;

@Data
public class WebSocketMessage extends Message {
    private String from ;
    private String to;
    private String content;

    public WebSocketMessage(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}
