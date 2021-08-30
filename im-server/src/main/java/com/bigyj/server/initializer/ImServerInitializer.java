package com.bigyj.server.initializer;

import com.bigyj.server.handler.ConnectedStatusChangetHandler;
import com.bigyj.server.handler.LoginRequestHandler;
import com.bigyj.server.handler.NoticeServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImServerInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private  LoginRequestHandler loginRequestHandler;
    @Autowired
    private NoticeServerHandler noticeServerHandler ;
    @Autowired
    private ConnectedStatusChangetHandler connectedStatusChangetHandler ;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast("encoder",new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast("connectedStatusChange",connectedStatusChangetHandler);
        pipeline.addLast("login",loginRequestHandler);
        pipeline.addLast("notice",noticeServerHandler);
    }
}
