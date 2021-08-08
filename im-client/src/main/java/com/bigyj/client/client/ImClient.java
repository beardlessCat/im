package com.bigyj.client.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
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

        /**
         * 客户端的是Bootstrap，服务端的则是 ServerBootstrap。
         * 都是AbstractBootstrap的子类。
         **/

        /**
         * 通过nio方式来接收连接和处理连接
         */

        eventLoopGroup = new NioEventLoopGroup();

    }

    /**
     * 重连
     */
    public void doConnect() {
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.remoteAddress(host, port);

            // 设置通道初始化
            bootstrap.handler(null);
            logger.info("客户端开始连接");

            ChannelFuture f = bootstrap.connect();//异步发起连接
            //f.addListener(connectedListener);


            // 阻塞
            // f.channel().closeFuture().sync();

        } catch (Exception e) {
            logger.info("客户端连接失败!" + e.getMessage());
        }
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

}
