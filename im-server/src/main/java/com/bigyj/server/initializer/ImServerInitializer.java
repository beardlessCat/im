package com.bigyj.server.initializer;

import com.bigyj.protocol.ChatMessageCodec;
import com.bigyj.protocol.ProtocolFrameDecoder;
import com.bigyj.server.handler.ConnectedStatusChangetHandler;
import com.bigyj.server.handler.LoginRequestHandler;
import com.bigyj.server.handler.NoticeServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImServerInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private LoginRequestHandler loginRequestHandler;
    @Autowired
    private NoticeServerHandler noticeServerHandler ;
    @Autowired
    private ConnectedStatusChangetHandler connectedStatusChangetHandler ;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //处理粘包与半包
        pipeline.addLast(new ProtocolFrameDecoder());
        //加入新的协议编码与界面器
        pipeline.addLast(new ChatMessageCodec());
//        pipeline.addLast("connectedStatusChange",connectedStatusChangetHandler);
        pipeline.addLast("login",loginRequestHandler);
//        pipeline.addLast("notice",noticeServerHandler);
    }
}
