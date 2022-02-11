package com.bigyj.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ServerPeerConnectedMessage extends AbstractResponseMessage{
    @Override
    public int getMessageType() {
        return ServerPeerConnectedMessage;
    }
}
