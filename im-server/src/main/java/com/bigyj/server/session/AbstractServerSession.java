package com.bigyj.server.session;

import com.bigyj.message.GroupChatRequestMessage;

public abstract class AbstractServerSession implements ServerSession{
    @Override
    public boolean writeGroupMessage(GroupChatRequestMessage groupChatRequestMessage) {
        return false;
    }
}
