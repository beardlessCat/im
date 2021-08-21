package com.bigyj.client.client;

import java.util.List;

import com.bigyj.client.initializer.ImClientInitializer;
import com.bigyj.client.load.balance.LoadBalance;
import com.bigyj.client.zk.ZkService;
import com.bigyj.entity.ServerNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("imClient")
@Slf4j
public class ImClient {

    public static final String MANAGE_PATH = "/im/nodes";
    public static final String PATH_PREFIX_NO_STRIP =  "seq-";

    private ZkService zkService;
    private LoadBalance loadBalance;
    @Autowired
    ImClient(ZkService zkService,LoadBalance loadBalance){
        this.zkService = zkService;
        this.loadBalance  = loadBalance ;
    }

    private Bootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    public ImClient() {
    }

    /**
     * 重连
     */
    public Channel doConnect() {
        Channel channel = null;
        List<ServerNode> workers = zkService.getWorkers(MANAGE_PATH, PATH_PREFIX_NO_STRIP);
        ServerNode serverNode = loadBalance.selectNode(workers);
        try {
            bootstrap = new Bootstrap();
            eventLoopGroup = new NioEventLoopGroup();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ImClientInitializer());
            channel = bootstrap.connect(serverNode.getHost(), serverNode.getPort()).sync().channel();
            return channel ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return channel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

}
