package com.bigyj.client.client;

import java.util.List;

import com.bigyj.client.initializer.ImClientInitializer;
import com.bigyj.client.zk.ZkService;
import com.bigyj.entity.Node;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("imClient")
@Slf4j
public class ImClient {

    public static final String MANAGE_PATH = "/im/nodes";
    public static final String PATH_PREFIX_NO_STRIP =  "seq-";

    private ZkService zkService;

    @Autowired
    ImClient(ZkService zkService){
        this.zkService = zkService;
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
        List<Node> workers = zkService.getWorkers(MANAGE_PATH, PATH_PREFIX_NO_STRIP);
        Node bestNode = this.getBestNode(workers);
        try {
            bootstrap = new Bootstrap();
            eventLoopGroup = new NioEventLoopGroup();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ImClientInitializer());
            channel = bootstrap.connect(bestNode.getHost(), bestNode.getPort()).sync().channel();

            return channel ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return channel;
    }

    private Node getBestNode(List<Node> workers) {
        return workers.get(0);
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

}
