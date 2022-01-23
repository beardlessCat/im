package com.bigyj.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Msg;
import com.bigyj.entity.MsgDto;
import com.bigyj.server.cach.SessionCacheSupport;
import com.bigyj.server.holder.LocalSessionHolder;
import com.bigyj.server.session.LocalSession;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class LogoutRequestHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private SessionCacheSupport sessionCacheSupport;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        MsgDto msgObject = JSONObject.parseObject(msg.toString(), MsgDto.class);
        //判断消息实例
        if (null == msg || (msgObject.getMsgType()!= Msg.MsgType.LOGOUT_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }
        logger.info("收到登出消息"+ msg);
        String uid = msgObject.getUser().getUid();
        LocalSessionHolder.removeServerSession(uid);
        //保存channel信息
        LocalSession serverSession = new LocalSession(ctx.channel());
        serverSession.setLogin(false);
        this.sengLogoutResponse(ctx,msgObject);
        //增加登录的handler
        ctx.pipeline().addAfter("chat", "login",  new LoginRequestHandler());
        //移除退出handler
        ctx.pipeline().remove("chat");
        //移除
        ctx.pipeline().remove(this);
        //移除redis缓存新
        sessionCacheSupport.remove(uid);
    }

    private void sengLogoutResponse(ChannelHandlerContext context,MsgDto msgObject) {
        Msg msg = Msg.builder(Msg.MsgType.LOGOUT_RESPONSE, msgObject.getUser())
                .setSuccess(true)
                .build();
        Channel channel = context.channel();
        channel.writeAndFlush(new Gson().toJson(msg)+"\r\n");
    }
}
