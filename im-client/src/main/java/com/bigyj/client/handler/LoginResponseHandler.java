package com.bigyj.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Msg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Msg msgObject = JSONObject.parseObject(msg.toString(), Msg.class);
		//判断消息实例
		if (null == msg || (msgObject.getMsgType()!= Msg.MsgType.LOGIN_RESPONSE)) {
			super.channelRead(ctx, msg);
			return;
		}
		logger.error("收到登录响应消息"+ msg);
		//保存channel与用户的关系信息 fixme

		//增加聊天的handler
		ctx.pipeline().addAfter("login", "chat",  new ImClientHandler());
		//移除登录handler
		ctx.pipeline().remove("login");
	}

}
