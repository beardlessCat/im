package com.bigyj.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.client.client.ClientSession;
import com.bigyj.entity.Msg;
import com.bigyj.entity.MsgDto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MsgDto msgObject = JSONObject.parseObject(msg.toString(), MsgDto.class);
		//判断消息实例
		if (null == msg || (msgObject.getMsgType()!= Msg.MsgType.LOGIN_RESPONSE)) {
			super.channelRead(ctx, msg);
			return;
		}
		logger.error("收到登录响应消息"+ msg);
		//判断登录成功还是登录失败
		if(msgObject.isSuccess()){
			//调整客户端登录状态
			ClientSession.loginSuccess(ctx,msgObject);

			//增加退出的logourResponseHandler
			ChannelPipeline pipeline = ctx.pipeline();
			pipeline.addAfter("encoder","logout",new LogoutResponseHandler());
			//增加聊天的handler
			pipeline.addAfter("encoder", "chat",  new ImClientHandler());
			//增加心跳
			pipeline.addAfter("encoder", "heartbeat", new HeartBeatClientHandler());
			//移除登录handler
			ctx.pipeline().remove("login");
		}else {
			logger.error("用户登录失败");
		}
	}
}
