package com.bigyj.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.client.client.ClientSession;
import com.bigyj.entity.Msg;
import com.bigyj.entity.MsgDto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 处理登录请求
 */
@Slf4j
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {
	Scanner scanner = new Scanner(System.in);
	CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);

	AtomicBoolean LOGIN = new AtomicBoolean(false);
	AtomicBoolean EXIT = new AtomicBoolean(false);
	//连接建立，触发active事件，调起控制台线程
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		new Thread(() -> {
			System.out.println("请输入用户名:");
			String username = scanner.nextLine();
			if (EXIT.get()) {
				return;
			}
			System.out.println("请输入密码:");
			String password = scanner.nextLine();
			if (EXIT.get()) {
				return;
			}
			//发送登录请求
			ctx.writeAndFlush(null);
			try {
				WAIT_FOR_LOGIN.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//登录失败
			if(!LOGIN.get()){
				logger.warn("{}用户登录失败！");
				ctx.channel().close();
			}

		}).start();

	}
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
			ChannelPipeline pipeline = ctx.pipeline();
			//增加聊天的handler
			pipeline.addAfter("encoder", "chat",  new ImClientHandler());
			//增加退出的logourResponseHandler
			pipeline.addAfter("encoder","logout",new LogoutResponseHandler());
			//增加心跳
			pipeline.addAfter("encoder", "heartbeat", new HeartBeatClientHandler());
			//移除登录handler
			ctx.pipeline().remove("login");
			LOGIN.set(true);
			//唤醒阻塞线程
			WAIT_FOR_LOGIN.countDown();
		}else {
			logger.error("用户登录失败");
		}
	}
}
