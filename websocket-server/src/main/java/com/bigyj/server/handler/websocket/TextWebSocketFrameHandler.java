package com.bigyj.server.handler.websocket;

import com.bigyj.message.websocket.WebSocketMessage;
import com.bigyj.server.manager.MemoryUserManager;
import com.bigyj.server.manager.ServerSessionManager;
import com.bigyj.server.session.AbstractServerSession;
import com.bigyj.server.utils.SpringContextUtil;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

//处理文本协议数据，处理TextWebSocketFrame类型的数据，websocket专门处理文本的frame就是TextWebSocketFrame
@Slf4j
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    //读到客户端的内容并且向客户端去写内容
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("收到消息："+msg.text());
        WebSocketMessage webSocketMessage = new Gson().fromJson(msg.text(), WebSocketMessage.class);
        SecurityServerHandler.SecurityCheckComplete securityCheckComplete = ctx.channel().attr(SecurityServerHandler.SECURITY_CHECK_COMPLETE_ATTRIBUTE_KEY).get();
        webSocketMessage.setFrom(securityCheckComplete.getUserId());
        /**
         * writeAndFlush接收的参数类型是Object类型，但是一般我们都是要传入管道中传输数据的类型，比如我们当前的demo
         * 传输的就是TextWebSocketFrame类型的数据
         */
        this.action(ctx,webSocketMessage);
    }

    //webSocket消息转发
    private void action(ChannelHandlerContext ctx,WebSocketMessage webSocketMessage) {
        String userId = webSocketMessage.getTo();
        ServerSessionManager serverSessionManager = SpringContextUtil.getBean(ServerSessionManager.class);
        //判断用户是否在线
        String toUid = MemoryUserManager.getUserByName(userId).getUid();
        AbstractServerSession serverSession = serverSessionManager.getServerSession(toUid);
        webSocketMessage.setTo(toUid);
        if(serverSession == null){
            this.sentNotOnlineMsg(webSocketMessage,userId,ctx);
        }else {
            boolean result = serverSession.writeAndFlush(webSocketMessage);
            if(!result){
            }
        }
    }

    private void sentNotOnlineMsg(WebSocketMessage webSocketMessage, String toUserId, ChannelHandlerContext ctx) {
        logger.error("用户{} 不在线，消息发送失败!",toUserId);
        ctx.writeAndFlush(new TextWebSocketFrame(webSocketMessage.getTo()+"用户不存在或者不在线"));
    }


    //每个channel都有一个唯一的id值
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //打印出channel唯一值，asLongText方法是channel的id的全名
        System.out.println("handlerAdded："+ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved：" + ctx.channel().id().asLongText());
        ServerSessionManager serverSessionManager = SpringContextUtil.getBean(ServerSessionManager.class);
        String userName = ctx.channel().attr(SecurityServerHandler.SECURITY_CHECK_COMPLETE_ATTRIBUTE_KEY).get().getUserId();
        serverSessionManager.removeServerSession(MemoryUserManager.getUserByName(userName).getUid());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }
}
