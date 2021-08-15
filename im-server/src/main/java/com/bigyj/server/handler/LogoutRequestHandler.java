package com.bigyj.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Msg;
import com.bigyj.server.holder.ServerSessionHolder;
import com.bigyj.server.server.ServerSession;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogoutRequestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        Msg msgObject = JSONObject.parseObject(msg.toString(), Msg.class);
        //判断消息实例
        if (null == msg || (msgObject.getMsgType()!= Msg.MsgType.LOGOUT_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }
        logger.error("收到登出消息"+ msg);
        String uid = msgObject.getUser().getUid();
        ServerSessionHolder.removeServerSession(uid);
        //保存channel信息
        ServerSession serverSession = new ServerSession(ctx.channel());
        serverSession.setLogin(false);
        this.sengLogoutResponse(ctx,msgObject);
        //增加登录的handler
        ctx.pipeline().addAfter("chat", "login",  new LoginRequestHandler());
        //移除退出handler
        ctx.pipeline().remove("chat");
        //移除
        ctx.pipeline().remove(this);
    }

    private void sengLogoutResponse(ChannelHandlerContext context,Msg msgObject) {
        msgObject.setMsgType(Msg.MsgType.LOGOUT_RESPONSE);
        msgObject.setSuccess(true);
        Channel channel = context.channel();
        channel.writeAndFlush(new Gson().toJson(msgObject)+"\r\n");
    }
}
