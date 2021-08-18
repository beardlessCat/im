package com.bigyj.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Msg;
import com.bigyj.entity.MsgDto;
import com.bigyj.entity.User;
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
        MsgDto msgObject = JSONObject.parseObject(msg.toString(), MsgDto.class);

        //判断消息实例
        if (null == msg || (msgObject.getMsgType()!= Msg.MsgType.LOGIN_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }
        logger.error("收到登录消息"+ msg);
        //fixme 验证认证信息
        User user = msgObject.getUser();
        String nickName = user.getNickName();
        String token = user.getToken();
        boolean validateflag = this.validateUser(nickName, token);
        if(!validateflag){
            this.sengLoginResponse(ctx,msgObject,validateflag);
        }
        //保存channel信息
        ServerSession serverSession = new ServerSession(ctx.channel());
        serverSession.setUser(user);
        logger.error(serverSession.getUser().getNickName()+"登录成功!");
        serverSession.bind();
        this.sengLoginResponse(ctx,msgObject,true);
        //增加聊天的handler
        //增加退出的handler
        ctx.pipeline().addAfter("login", "heartBeat",new HeartBeatServerHandler());
        ctx.pipeline().addAfter("login","logout",new LogoutRequestHandler());
        ctx.pipeline().addAfter("logout", "chat",  new ChatRedirectHandler());
        //移除登录handler
        ctx.pipeline().remove("login");
    }

    private boolean validateUser(String nickName, String token) {
        //fixme 验证用户登录信息
        logger.error("用户{}请求登录，token为{}",nickName,token);
        return true;
    }

    private void sengLoginResponse(ChannelHandlerContext context,MsgDto msgObject,boolean validateflag) {
        Msg msg = Msg.builder(Msg.MsgType.LOGIN_RESPONSE, msgObject.getUser())
                .setSuccess(validateflag)
                .build();
        Channel channel = context.channel();
        channel.writeAndFlush(new Gson().toJson(msg)+"\r\n");
    }
}
