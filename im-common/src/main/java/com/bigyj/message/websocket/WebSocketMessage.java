package com.bigyj.message.websocket;

import lombok.Data;

@Data
public class WebSocketMessage {
    private String from ;
    private String to;
    private String content;

    public WebSocketMessage(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }
}
