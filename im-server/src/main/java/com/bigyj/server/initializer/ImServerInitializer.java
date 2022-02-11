package com.bigyj.server.initializer;

import com.bigyj.protocol.ChatMessageCodec;
import com.bigyj.protocol.ProtocolFrameDecoder;
import com.bigyj.server.handler.LoginRequestHandler;
import com.bigyj.server.handler.ServerPeerConnectedHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImServerInitializer extends ChannelInitializer<SocketChannel> {
    private LoginRequestHandler loginRequestHandler;
    private ServerPeerConnectedHandler serverPeerConnectedHandler ;
    @Autowired
    public ImServerInitializer(LoginRequestHandler loginRequestHandler, ServerPeerConnectedHandler serverPeerConnectedHandler) {
        this.loginRequestHandler = loginRequestHandler;
        this.serverPeerConnectedHandler = serverPeerConnectedHandler;
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //处理粘包与半包
        pipeline.addLast(new ProtocolFrameDecoder());
        //加入新的协议编码与界面器
        pipeline.addLast(new ChatMessageCodec());
        pipeline.addLast(serverPeerConnectedHandler);
        pipeline.addLast("login",loginRequestHandler);
    }
}
