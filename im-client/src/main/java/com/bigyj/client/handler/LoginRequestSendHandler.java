package com.bigyj.client.handler;

import com.bigyj.client.client.ClientSession;
import com.bigyj.message.*;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 处理登录请求
 */
@Slf4j
public class LoginRequestSendHandler extends ChannelInboundHandlerAdapter {
	Scanner scanner = new Scanner(System.in);
	CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
	private static final int WRITE_IDLE_GAP = 3;

	AtomicBoolean LOGIN = new AtomicBoolean(false);
	AtomicBoolean EXIT = new AtomicBoolean(false);
	//连接建立，触发active事件，调起控制台线程
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("连接建立成功");
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
			LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username,password);
			ctx.writeAndFlush(loginRequestMessage);
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
			logger.info("登录成功，请输入如下指令：");
			while (true) {
				System.out.println("==================================");
				System.out.println("send [username] [content]");
				System.out.println("gsend [group name] [content]");
				System.out.println("gcreate [group name] [m1,m2,m3...]");
				System.out.println("gmembers [group name]");
				System.out.println("gjoin [group name]");
				System.out.println("gquit [group name]");
				System.out.println("quit");
				System.out.println("==================================");
				String command = null;
				try {
					command = scanner.nextLine();
				} catch (Exception e) {
					break;
				}
				if(EXIT.get()){
					return;
				}
				String[] s = command.split(" ");
				switch (s[0]){
					case "send":
						ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
						break;
					case "quit":
						ctx.channel().close();
						return;
				}
			}
		},"scanner in").start();

	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		LoginResponseMessage loginResponseMessage = (LoginResponseMessage) msg;
		//判断消息实例
		if (null == msg || (loginResponseMessage.getMessageType()!= Message.LoginResponseMessage)) {
			super.channelRead(ctx, msg);
			return;
		}
		logger.info("收到登录响应消息"+ msg);
		//判断登录成功还是登录失败
		if(loginResponseMessage.isSuccess()){
			//调整客户端登录状态
			ClientSession.loginSuccess(ctx,null);
			ChannelPipeline pipeline = ctx.pipeline();
			//增加聊天的handler
			pipeline.addLast("chat",  new ChatClientHandler());
			//增加退出的logourResponseHandler
			pipeline.addLast("logout",new LogoutResponseHandler());
			//增加心跳
			pipeline.addLast(new IdleStateHandler(0, WRITE_IDLE_GAP, 0));
			// ChannelDuplexHandler 可以同时作为入站和出站处理器
			pipeline.addLast(new ChannelDuplexHandler() {
				// 用来触发特殊事件
				@Override
				public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
					IdleStateEvent event = (IdleStateEvent) evt;
					// 触发了写空闲事件
					if (event.state() == IdleState.WRITER_IDLE) {
						logger.debug("3s 没有写数据了，发送一个心跳包");
						ctx.writeAndFlush(new PingMessage());
					}
				}
			});
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
