package com.bigyj.client.initializer;

import com.bigyj.client.handler.ExceptionHandler;
import com.bigyj.client.handler.LoginResponseHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

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
        //年报半包处理
        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast("encoder",new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast("login",new LoginResponseHandler());
        pipeline.addLast("except",exceptionHandler);
    }
}
