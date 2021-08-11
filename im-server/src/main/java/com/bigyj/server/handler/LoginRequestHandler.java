package com.bigyj.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Msg;
import com.bigyj.server.server.ServerSession;
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
        if (null == msg || (msgObject.getMsgType()!= Msg.MsgType.LOGIN)) {
            super.channelRead(ctx, msg);
            return;
        }
        logger.error("收到登录消息"+ msg);
        //保存channel信息
        ServerSession serverSession = new ServerSession(ctx.channel());
        logger.error(serverSession.getUser().getNickName()+"登录成功!");
        //增加聊天的handler
        ctx.pipeline().addAfter("login", "chat",  new ImChatServerHandler());
        //移除登录handler
        ctx.pipeline().remove("login");
    }
}
