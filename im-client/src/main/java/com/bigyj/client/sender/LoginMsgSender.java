package com.bigyj.client.sender;

import com.bigyj.client.client.ChannelHolder;
import com.bigyj.entity.Msg;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

@Service
public class LoginMsgSender extends MsgSender{

    @Override
    public void sendMsg(Msg msg) {
        Channel channel = ChannelHolder.channel;
        channel.writeAndFlush(new Gson().toJson(msg)+"\r\n");
    }
}
