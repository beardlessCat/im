import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.Test;

import java.net.InetSocketAddress;

public class ServerTest {


    @Test
    public void serverStart() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                //2 设置nio类型的channel
                .channel(NioServerSocketChannel.class)
                .childHandler(new ImServerInitializerTest())
                .localAddress(new InetSocketAddress(18080));
        // 通过调用sync同步方法阻塞直到绑定成功
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        // 7 监听通道关闭事件
        // 应用程序会一直等待，直到channel关闭
        ChannelFuture closeFuture =
                channelFuture.channel().closeFuture();
        closeFuture.sync();
    }

}
