package com.bigyj.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Msg;
import com.bigyj.entity.MsgDto;
import com.bigyj.server.holder.ServerSessionHolder;
import com.bigyj.server.server.ServerSession;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MsgDto msgObject = JSONObject.parseObject(msg.toString(), MsgDto.class);
		//判断消息实例
		if (null == msg || (msgObject.getMsgType()!= Msg.MsgType.CHAT)) {
			super.channelRead(ctx, msg);
			return;
		}
		//反向导航
		ServerSession session = ctx.channel().attr(ServerSession.SESSION_KEY).get();
		//判断是否登录
		if (null == session || !session.isLogin()) {
			logger.error("用户尚未登录，不能发送消息");
			return;
		}
		this.action( msgObject);
	}

	private void action(MsgDto msgObject) {
		String toUserId = msgObject.getToUserId();
		ServerSession serverSession = ServerSessionHolder.getServerSession(toUserId);
		if(serverSession== null){
			logger.error("[" + toUserId + "] 不在线，发送失败!");
		}else {
			serverSession.getChannel().writeAndFlush(new Gson().toJson(msgObject)+"\n");
		}
	}
}
