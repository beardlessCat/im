package com.bigyj.message;

import lombok.Data;

@Data
public class LoginResponseMessage extends AbstractResponseMessage{
    public LoginResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
}
