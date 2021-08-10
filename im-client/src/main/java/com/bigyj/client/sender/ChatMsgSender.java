package com.bigyj.client.sender;

import com.bigyj.client.client.ChannelHolder;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

@Service
public class ChatMsgSender implements MsgSender{
    @Override
    public void sendMsg(String msg) {
        Channel channel = ChannelHolder.channel;
        channel.writeAndFlush(msg+"\r\n");
    }
}
