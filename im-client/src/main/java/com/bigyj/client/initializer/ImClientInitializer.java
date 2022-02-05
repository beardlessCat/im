package com.bigyj.client.initializer;

import com.bigyj.client.handler.ExceptionHandler;
import com.bigyj.client.handler.LoginRequestSendHandler;
import com.bigyj.protocol.ChatMessageCodec;
import com.bigyj.protocol.ProtocolFrameDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ImClientInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    @Lazy
    private ExceptionHandler exceptionHandler ;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //粘报半包处理
        pipeline.addLast(new ProtocolFrameDecoder());
        //加入新的协议编码与解码器
        pipeline.addLast("messageCodec",new ChatMessageCodec());
        pipeline.addLast("login",new LoginRequestSendHandler());
        pipeline.addLast("except",exceptionHandler);
    }
}
