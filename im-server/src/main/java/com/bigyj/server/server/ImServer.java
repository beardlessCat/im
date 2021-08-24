package com.bigyj.server.server;

import com.bigyj.server.initializer.ImServerInitializer;
import com.bigyj.entity.ServerNode;
import com.bigyj.server.registration.ZkService;
import com.bigyj.server.worker.ServerWorker;
import com.bigyj.utils.NodeUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Service("imServer")
@Slf4j
public class ImServer {
    @Value("${chat.server.port}")
    private int PORT ;
    private static final String MANAGE_PATH ="/im/nodes";
    public static final String PATH_PREFIX = MANAGE_PATH + "/seq-";
    private ZkService zkService;
    private ImServerInitializer imServerInitializer;
    @Autowired
    ImServer(ZkService zkService,ImServerInitializer imServerInitializer){
        this.zkService = zkService;
        this.imServerInitializer = imServerInitializer ;
    }

    private EventLoopGroup bossGroup ;

    private EventLoopGroup workerGroup ;

    public void startImServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workerGroup)
            //2 设置nio类型的channel
            .channel(NioServerSocketChannel.class)
            .childHandler(imServerInitializer)
                    .localAddress(new InetSocketAddress(PORT));
            // 通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            logger.info(
                    "IM 服务启动, 端口 " +
                            channelFuture.channel().localAddress());
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        logger.error("服务端启动成功");
                        //注册到zookeeper
                        //判断根节点是否存在
                        if (zkService.checkNodeExists(MANAGE_PATH)) {
                            zkService.createPersistentNode(MANAGE_PATH);
                        }
                        ServerNode serverNode = new ServerNode("127.0.0.1",PORT);
                        String pathRegistered =  zkService.createNode(PATH_PREFIX, serverNode);
                        //为node 设置id
                        serverNode.setId(NodeUtil.getIdByPath(pathRegistered,PATH_PREFIX));
                        logger.info("本地节点, path={}, id={}", pathRegistered, serverNode.getId());
                        //fixme 管理serverNode
                        ServerWorker.instance().setServerNode(serverNode);
                    } else {
                        logger.error("服务端启动成失败");
                    }
                }
            });
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
