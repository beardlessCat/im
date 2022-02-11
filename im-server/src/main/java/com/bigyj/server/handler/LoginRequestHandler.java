package com.bigyj.server.handler;

import com.bigyj.message.LoginRequestMessage;
import com.bigyj.message.LoginResponseMessage;
import com.bigyj.server.manager.MemoryUserManager;
import com.bigyj.server.manager.ServerSessionManager;
import com.bigyj.server.session.LocalSession;
import com.bigyj.user.User;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private ServerSessionManager serverSessionManager ;

    private DisconnectedHandler disconnectedHandler = new DisconnectedHandler();

    private GroupChatMessageHandler groupChatMessageHandler = new GroupChatMessageHandler();
    @Autowired
    private ChatRedirectHandler chatRedirectHandler ;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if(!(msg instanceof LoginRequestMessage)){
            super.channelRead(ctx, msg);
            return;
        }
        LoginRequestMessage loginRequestMessage = (LoginRequestMessage) msg;
        logger.info("收到请求登录消息"+ msg);
        String username = loginRequestMessage.getUsername();
        String passWord = loginRequestMessage.getPassword();
        boolean validateSuccess = this.validateUser(username, passWord);
        if(!validateSuccess){
            //登录失败，发送失败消息
            LoginResponseMessage responseMessage = new LoginResponseMessage(false,"失败");
            ctx.writeAndFlush(responseMessage);
            return;
        }
        //保存channel信息
        LocalSession serverSession = new LocalSession(ctx.channel());
        serverSession.setUser(MemoryUserManager.getUserByName(username));
        logger.info(serverSession.getUser().getUserName()+"登录成功!");
        serverSession.bind();

        //连接信息保存至redis数据库
        serverSessionManager.addServerSession(serverSession);
        //发送登录响应信息
        LoginResponseMessage responseMessage = new LoginResponseMessage(validateSuccess,"登录成功");
        ctx.writeAndFlush(responseMessage).addListener(future -> {
            //消息发送成功
            if (future.isSuccess()) {
                //增加心跳handler
                ctx.pipeline().addLast(new IdleStateHandler(20, 0, 0));
                // ChannelDuplexHandler 可以同时作为入站和出站处理器
                ctx.pipeline().addLast(new ChannelDuplexHandler() {
                    // 用来触发特殊事件
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                        IdleStateEvent event = (IdleStateEvent) evt;
                        // 触发了读空闲事件·
                        if (event.state() == IdleState.READER_IDLE) {
                            logger.info("已经 5s 没有读到数据了");
                            ctx.channel().close();
                        }
                    }
                });
                //增加聊天的handler
                ctx.pipeline().addLast("chat",  chatRedirectHandler);
                //增加群聊handler
                ctx.pipeline().addLast("groupChat",  groupChatMessageHandler);
                //增加退出的handler
                ctx.pipeline().addLast("logout", disconnectedHandler);
            }else {
                logger.error("登录响应消息发送失败！");
            }
        });
    }

    private boolean validateUser(String userName, String password) {
        User user = MemoryUserManager.getUserByName(userName);
        if(user!=null){
            return user.getPassWord().equals(password);
        }
        return false;
    }

}
