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
    private static final int HEARTBEAT_INTERVAL = 60;

    //在Handler被加入到Pipeline时，开始发送心跳
    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
            throws Exception {
        Msg msg = Msg.builder().setMsgType(Msg.MsgType.HEART_BEAT)
                .build();
        //发送心跳
        heartBeat(ctx, new Gson().toJson(msg));
    }

    //使用定时器，发送心跳报文
    public void heartBeat(ChannelHandlerContext ctx,
                          String heartbeatMsg) {
        ctx.executor().schedule(() -> {
            if (ctx.channel().isActive()) {
                logger.info(" 发送 HEART_BEAT  消息 to server");
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
        if (null == msg || msgObject.getMsgType()!= Msg.MsgType.HEART_BEAT) {
            super.channelRead(ctx, msg);
            return;
        }
        //判断类型
        if (msgObject.getMsgType()== Msg.MsgType.HEART_BEAT) {
            logger.info(" 收到回写的 HEART_BEAT  消息 from server");
            return;
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
