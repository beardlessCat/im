package com.bigyj.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.MsgDto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        MsgDto msgObject = JSONObject.parseObject(msg, MsgDto.class);
        logger.error("收到用户的{}消息：{}",msgObject.getUser().getNickName(),msgObject.getContent());
    }
}
