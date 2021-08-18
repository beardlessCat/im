package com.bigyj.client.sender;

import com.bigyj.client.client.ClientSession;
import com.bigyj.entity.Msg;
import com.google.gson.Gson;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatMsgSender extends MsgSender{
    @Override
    public void sendMsg(Msg msg) {
        ClientSession session = getSession();
        ChannelFuture channelFuture = session.getChannel().writeAndFlush(new Gson().toJson(msg) + "\r\n");
        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    sendSucced(msg);
                } else {
                    sendfailed(msg);
                }
            }
        });
    }
    protected void sendSucced(Msg message) {
        logger.info("发送成功");

    }

    protected void sendfailed(Msg message) {
        logger.info("发送失败");
    }
}
