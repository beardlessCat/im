package com.bigyj.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Msg;
import com.bigyj.entity.MsgDto;
import com.bigyj.entity.User;
import com.bigyj.message.LoginRequestMessage;
import com.bigyj.message.LoginResponseMessage;
import com.bigyj.message.Message;
import com.bigyj.server.session.LocalSession;
import com.bigyj.server.manager.ServerSessionManager;
import com.google.gson.Gson;
import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
    private static final int READ_IDLE_GAP = 150;

    @Autowired
    private ServerSessionManager serverSessionManager ;
    @Autowired
    private LogoutRequestHandler logoutRequestHandler;
    @Autowired
    private ChatRedirectHandler chatRedirectHandler ;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        LoginRequestMessage loginRequestMessage = (LoginRequestMessage) msg;

        //判断消息实例
        if (null == msg || (loginRequestMessage.getMessageType()!= Message.LoginRequestMessage)) {
            super.channelRead(ctx, msg);
            return;
        }
        logger.info("收到请求登录消息"+ msg);
        String username = loginRequestMessage.getUsername();
        String passWord = loginRequestMessage.getPassword();
        boolean validateSuccess = this.validateUser(username, passWord);
        if(!validateSuccess){
//            this.sengLoginResponse(ctx,msgObject,validateflag);
        }
        //保存channel信息
        LocalSession serverSession = new LocalSession(ctx.channel());
        //fixme
        serverSession.setUser(new User().setNickName(username));
        logger.info(serverSession.getUser().getNickName()+"登录成功!");
        serverSession.bind();
        //连接信息保存至redis数据库
        serverSessionManager.addServerSession(serverSession);
        //发送登录响应信息
        LoginResponseMessage responseMessage = new LoginResponseMessage(validateSuccess,"登录成功");
        ctx.writeAndFlush(responseMessage).addListener(future -> {
            //消息发送成功
            if (future.isSuccess()) {
                //增加心跳handler
                ctx.pipeline().addAfter("login", "heartBeat",new HeartBeatServerHandler());
                //增加聊天的handler
                ctx.pipeline().addAfter("logout", "chat",  chatRedirectHandler);
                //增加退出的handler
                ctx.pipeline().addAfter("login","logout",logoutRequestHandler);
                //移除登录handler
                ctx.pipeline().remove("login");
            }else {
                logger.error("登录响应消息发送失败！");
            }
        });
    }

    private boolean validateUser(String nickName, String token) {
        //fixme 验证用户登录信息
        logger.info("用户{}请求登录，token为{}",nickName,token);
        return true;
    }

}
