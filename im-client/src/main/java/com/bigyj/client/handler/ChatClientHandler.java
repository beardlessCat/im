package com.bigyj.client.handler;

import com.bigyj.message.ChatResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatClientHandler extends SimpleChannelInboundHandler<ChatResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatResponseMessage chatResponseMessage) throws Exception {
        if(chatResponseMessage.isSuccess()){
            logger.info("收到用户的{}消息：{}",chatResponseMessage.getFrom(),chatResponseMessage.getContent());
        }else {
            logger.info("消息发送失败：{}",chatResponseMessage.getReason());
        }
    }
}
