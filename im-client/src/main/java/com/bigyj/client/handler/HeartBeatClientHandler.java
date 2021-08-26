package com.bigyj.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.client.client.ClientSession;
import com.bigyj.entity.Msg;
import com.bigyj.entity.MsgDto;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    //心跳的时间间隔，单位为s
    private static final int HEARTBEAT_INTERVAL = 50;

    //在Handler被加入到Pipeline时，开始发送心跳
    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
            throws Exception {
        Msg msg = Msg.builder().setMsgType(Msg.MsgType.HEART_PING)
                .setContent("ping.......")
                .build();
        //发送心跳
        heartBeat(ctx, new Gson().toJson(msg));
    }

    //使用定时器，发送心跳报文
    public void heartBeat(ChannelHandlerContext ctx,
                          String heartbeatMsg) {
        ctx.executor().schedule(() -> {
            if (ctx.channel().isActive()) {
                //logger.info("ping.......");
                ctx.writeAndFlush(heartbeatMsg+"\n");
                //递归调用，发送下一次的心跳
                heartBeat(ctx, heartbeatMsg);
            }

        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 接受到服务器的心跳回写
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MsgDto msgObject = JSONObject.parseObject(msg.toString(), MsgDto.class);

        //判断消息实例
        if (null == msg || msgObject.getMsgType()!= Msg.MsgType.HEART_PONG) {
            super.channelRead(ctx, msg);
            return;
        }
        //判断类型
        if (msgObject.getMsgType()== Msg.MsgType.HEART_PONG) {
            logger.info(msgObject.getContent());
            return;
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
