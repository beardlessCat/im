package com.bigyj.message;

import lombok.Data;

@Data
public abstract class AbstractResponseMessage extends Message{
    private boolean success;
    private String reason;

    public AbstractResponseMessage() {
    }

    public AbstractResponseMessage(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }
}
