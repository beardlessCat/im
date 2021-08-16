package com.bigyj.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.client.client.ClientSession;
import com.bigyj.entity.Msg;
import com.bigyj.entity.MsgDto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogoutResponseHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MsgDto msgObject = JSONObject.parseObject(msg.toString(), MsgDto.class);
		//判断消息实例
		if (null == msg || (msgObject.getMsgType()!= Msg.MsgType.LOGOUT_RESPONSE)) {
			super.channelRead(ctx, msg);
			return;
		}
		logger.error("收到退出响应消息"+ msg);
		//判断登录成功还是登录失败
		if(msgObject.isSuccess()){
			//调整客户端登录状态
			ClientSession.logOutSuccess(ctx,msgObject);
			//增加退出的logourResponseHandler
			ctx.pipeline().addBefore("chat","login",new LoginResponseHandler());
			//移除登录handler
			ctx.pipeline().remove("chat");
			//移除心跳handler
			ctx.pipeline().remove("heartbeat");
			ctx.pipeline().remove(this);
			logger.error("用户登录退出！");
		}else {
			logger.error("用户退出失败");
		}
	}
}
