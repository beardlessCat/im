package com.bigyj.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Msg;
import com.bigyj.entity.MsgDto;
import com.bigyj.server.session.LocalSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class NoticeServerHandler  extends ChannelInboundHandlerAdapter {
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MsgDto msgObject = JSONObject.parseObject(msg.toString(), MsgDto.class);
		//判断消息实例
		if (null == msg || (msgObject.getMsgType()!= Msg.MsgType.SERVER_NOTICE)) {
			super.channelRead(ctx, msg);
			return;
		}
		//移除登录handler
		ctx.pipeline().addAfter("login","clientServerHeartBeat",new ClientServerHeartBeatHandler());
		ctx.pipeline().remove("login");
	}
}
