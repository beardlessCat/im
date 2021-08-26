package com.bigyj.server.handler.client;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Msg;
import com.bigyj.entity.MsgDto;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerBeatHandler extends ChannelInboundHandlerAdapter {
	//心跳的时间间隔，单位为s
	private static final int HEARTBEAT_INTERVAL = 50;

	//在Handler被加入到Pipeline时，开始发送心跳
	@Override
	public void channelActive(ChannelHandlerContext ctx)
			throws Exception {
		logger.info("发送服务事件通知！");
		//先发送上线通知
		Msg msg = Msg.builder().setMsgType(Msg.MsgType.SERVER_NOTICE)
				.setContent("i connected.......")
				.build();
		ctx.writeAndFlush(new Gson().toJson(msg)+"\n").addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				if (future.isSuccess()) {
					//上线通知发送成功后（服务端完成心跳handler的加入），发送心跳
					Msg beat = Msg.builder().setMsgType(Msg.MsgType.HEART_PING)
							.setContent("ping.......")
							.build();
					//发送心跳
					heartBeat(ctx, new Gson().toJson(beat));
				}else {
					logger.error("服务事件通知发送失败！");
				}
			}
		});
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
			//logger.info(msgObject.getContent());
			return;
		} else {
			super.channelRead(ctx, msg);
		}
	}
}
