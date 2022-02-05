package com.bigyj.server.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.bigyj.entity.ServerNode;
import com.bigyj.server.config.NettyServerConfig;
import com.bigyj.server.config.SocketConfig;
import com.bigyj.server.initializer.ImServerInitializer;
import com.bigyj.server.registration.ZkService;
import com.bigyj.server.worker.ServerRouterWorker;
import com.bigyj.server.worker.ServerWorker;
import com.bigyj.utils.NodeUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("imServer")
@Slf4j
public class WebsocketServer {
    @Value("${chat.server.port}")
    private int PORT ;
    private static final String MANAGE_PATH ="/websocket/nodes";
    public static final String PATH_PREFIX = MANAGE_PATH + "/seq-";
    private ZkService zkService;
    @Autowired
    WebsocketServer(ZkService zkService ){
        this.zkService = zkService;
    }

    private EventLoopGroup bossGroup ;

    private EventLoopGroup workerGroup ;
    NettyServerConfig nettyServerConfig = new NettyServerConfig();
    public void startImServer() {
        initGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            //判断是否支持epoll
            Class<? extends ServerChannel> channelClass = NioServerSocketChannel.class;
            if (useEpoll()) {
                channelClass = EpollServerSocketChannel.class;
            }
            serverBootstrap.group(bossGroup, workerGroup)
            //2 设置nio类型的channel
            .channel(channelClass)
            .childHandler(new ImServerInitializer())
            .localAddress(new InetSocketAddress(PORT));

            applyConnectionOptions(serverBootstrap);

            // 通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            logger.info("服务启动, 端口 " +channelFuture.channel().localAddress());
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        logger.info("服务端启动成功");
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
                        ServerWorker.instance().setServerNode(serverNode);
                        ServerRouterWorker.instance().init();
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
    //配置连接属性
    private void applyConnectionOptions(ServerBootstrap bootstrap) {

        SocketConfig config = nettyServerConfig.getSocketConfig();
        bootstrap.childOption(ChannelOption.TCP_NODELAY, config.isTcpNoDelay());
        if (config.getTcpSendBufferSize() != -1) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, config.getTcpSendBufferSize());
        }
        if (config.getTcpReceiveBufferSize() != -1) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, config.getTcpReceiveBufferSize());
            bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(config.getTcpReceiveBufferSize()));
        }
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, config.isTcpKeepAlive());
        bootstrap.childOption(ChannelOption.SO_LINGER, config.getSoLinger());

        bootstrap.option(ChannelOption.SO_REUSEADDR, config.isReuseAddress());
        bootstrap.option(ChannelOption.SO_BACKLOG, config.getAcceptBackLog());
    }

    private void initGroup() {
        if(useEpoll()){
            bossGroup = new EpollEventLoopGroup(nettyServerConfig.getBossThreads(),new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyEPOLLBoss_%d", this.threadIndex.incrementAndGet()));
                }
            });
            workerGroup = new EpollEventLoopGroup(nettyServerConfig.getWorkerThreads(),new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int threadTotal = nettyServerConfig.getWorkerThreads();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerEPOLLSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
                }
            });
        }else {

            bossGroup = new NioEventLoopGroup(nettyServerConfig.getBossThreads(), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyNIOBoss_%d", this.threadIndex.incrementAndGet()));
                }
            });

            workerGroup = new NioEventLoopGroup(nettyServerConfig.getWorkerThreads(), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int threadTotal = nettyServerConfig.getWorkerThreads();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerNIOSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
                }
            });
        }

    }

    private boolean useEpoll() {
        return  nettyServerConfig.isUseEpollNativeSelector()
                && Epoll.isAvailable();
    }
}
