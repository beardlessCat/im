package com.bigyj.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Msg;
import com.bigyj.server.server.ServerSession;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        Msg msgObject = JSONObject.parseObject(msg.toString(), Msg.class);
        //判断消息实例
        if (null == msg || (msgObject.getMsgType()!= Msg.MsgType.LOGIN_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }
        logger.error("收到登录消息"+ msg);
        //保存channel信息
        ServerSession serverSession = new ServerSession(ctx.channel());
        serverSession.setUser(msgObject.getUser());
        logger.error(serverSession.getUser().getNickName()+"登录成功!");
        serverSession.bind();
        this.sengLoginResponse(ctx,msgObject);
        //增加聊天的handler
        //增加退出的handler
        //ctx.pipeline().addAfter("login", "heartBeat",new HeartBeatServerHandler());
        ctx.pipeline().addAfter("login","logout",new LogoutRequestHandler());
        ctx.pipeline().addAfter("logout", "chat",  new ChatRedirectHandler());

        //移除登录handler
        ctx.pipeline().remove("login");
    }

    private void sengLoginResponse(ChannelHandlerContext context,Msg msgObject) {
        msgObject.setMsgType(Msg.MsgType.LOGIN_RESPONSE);
        msgObject.setSuccess(true);
        Channel channel = context.channel();
        channel.writeAndFlush(new Gson().toJson(msgObject)+"\r\n");
    }
}
