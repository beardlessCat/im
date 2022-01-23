package com.bigyj.server.handler.websocket;

import com.bigyj.message.websocket.WebSocketMessage;
import com.bigyj.server.holder.LocalSessionHolder;
import com.bigyj.server.session.LocalSession;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

//处理文本协议数据，处理TextWebSocketFrame类型的数据，websocket专门处理文本的frame就是TextWebSocketFrame
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
        //        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务时间："+ LocalDateTime.now()));
        String userName = webSocketMessage.getTo();
        LocalSession serverSession = LocalSessionHolder.getServerSession(userName);
        WebSocketMessage content = new WebSocketMessage(webSocketMessage.getFrom(), webSocketMessage.getTo(), webSocketMessage.getContent());
        serverSession.getChannel().writeAndFlush(new TextWebSocketFrame(new Gson().toJson(content)));
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
