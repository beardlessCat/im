package com.bigyj.server.server;

import com.bigyj.server.initializer.ImServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Service("imServer")
@Slf4j
public class ImServer {
    private EventLoopGroup bossGroup ;
    private EventLoopGroup workerGroup ;
    //@Value("${chat.server.port}")
    private int port = 8081;
    public void startImServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workerGroup)
            //2 设置nio类型的channel
            .channel(NioServerSocketChannel.class)
            .childHandler(new ImServerInitializer())
                    .localAddress(new InetSocketAddress(port));
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
