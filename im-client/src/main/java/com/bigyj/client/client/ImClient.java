package com.bigyj.client.client;

import com.bigyj.client.initializer.ImClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("imClient")
@Slf4j
public class ImClient {
    // 服务器ip地址
    @Value("${chat.server.ip}")
    private String host;
    // 服务器端口
    @Value("${chat.server.port}")
    private int port;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;

    public ImClient() {
        eventLoopGroup = new NioEventLoopGroup();
    }

    /**
     * 重连
     */
    public void doConnect() {
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ImClientInitializer());
            Channel channel = bootstrap.connect(host, port).sync().channel();
            ChannelHolder.channel = channel;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

}
