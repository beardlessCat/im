package com.bigyj.server.server;

import com.bigyj.server.initializer.ImServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Service("inServer")
@Slf4j
public class ImServer {
    private EventLoopGroup bossGroup ;
    private EventLoopGroup workerGroup ;
    @Value("${chat.server.port}")
    private int port;

    public void startImServer() {
        ServerBootstrap serverBootstrap =
                new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workerGroup);
            //2 设置nio类型的channel
            serverBootstrap.channel(NioServerSocketChannel.class);
            //3 设置监听端口
            serverBootstrap.localAddress(new InetSocketAddress(port));
            //4 设置通道选项
            //            b.option(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.option(ChannelOption.ALLOCATOR,
                    PooledByteBufAllocator.DEFAULT);

            //5 装配流水线
            serverBootstrap.childHandler(new ImServerInitializer());
            // 6 开始绑定server
            // 通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            logger.info(
                    "IM 服务启动, 端口 " +
                            channelFuture.channel().localAddress());
            // 7 监听通道关闭事件
            // 应用程序会一直等待，直到channel关闭
            ChannelFuture closeFuture =
                    channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 8 优雅关闭EventLoopGroup，
            // 释放掉所有资源包括创建的线程
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
