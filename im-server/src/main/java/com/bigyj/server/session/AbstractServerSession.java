package com.bigyj.server.session;

import com.bigyj.message.ChatRequestMessage;
import com.bigyj.message.GroupChatResponseMessage;

public abstract class AbstractServerSession implements ServerSession{
    @Override
    public boolean writeGroupMessage(GroupChatResponseMessage groupChatResponseMessage) {
        this.writeAndFlush(new ChatRequestMessage());
        return false;
    }
}
