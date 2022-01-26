package com.bigyj.server.handler.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SecurityServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static final AttributeKey<SecurityCheckComplete> SECURITY_CHECK_COMPLETE_ATTRIBUTE_KEY =
            AttributeKey.valueOf("SECURITY_CHECK_COMPLETE_ATTRIBUTE_KEY");

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String url = request.uri();
        //extracts token information headers
        HttpHeaders headers = request.headers();
        String token = headers.get("token");
        if(-1 != url.indexOf("/ws")) {
            String userName = url.split("\\?")[1];
            //check token
            SecurityCheckResult checkResult = this.validateToken(userName);
            if(checkResult.success){
                SecurityCheckComplete complete = new SecurityCheckComplete(token, checkResult.getUserId());
                ctx.channel().attr(SECURITY_CHECK_COMPLETE_ATTRIBUTE_KEY).set(complete);
                ctx.fireUserEventTriggered(complete);
            }
            // 传递到下一个handler：升级握手
            ctx.fireChannelRead(request.retain());
        } else {
            System.out.println("not socket");
            ctx.close();
        }
    }

    private SecurityCheckResult validateToken(String userName) {
        return new SecurityCheckResult(userName,true);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Getter
    @AllArgsConstructor
    public static final class SecurityCheckComplete {
        private String connectionUUID;
        private String userId;
    }
    @Getter
    @AllArgsConstructor
    public static final class SecurityCheckResult {
        private String userId;
        private boolean success;
    }

}