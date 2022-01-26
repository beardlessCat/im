import com.bigyj.message.ServerPeerConnectedMessage;
import com.bigyj.message.websocket.WebSocketMessage;
import com.bigyj.protocol.ChatMessageCodec;
import com.bigyj.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.junit.Test;
public class ClientTest {

    @Test
    public void clientStart() throws InterruptedException {
        int WRITE_IDLE_GAP = 15;

        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ProtocolFrameDecoder());
                        //编解码handler
                        pipeline.addLast("codec", new ChatMessageCodec());
                        //增加心跳
                        pipeline.addLast(new IdleStateHandler(0, WRITE_IDLE_GAP, 0));
                        // ChannelDuplexHandler 可以同时作为入站和出站处理器
                        pipeline.addLast(new ChannelDuplexHandler() {
                            // 用来触发特殊事件
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                                IdleStateEvent event = (IdleStateEvent) evt;
                                // 触发了写空闲事件
                                if (event.state() == IdleState.WRITER_IDLE) {
                                    ctx.writeAndFlush(new WebSocketMessage("tom","betty","hello"));
                                }
                            }
                            //服务端认证
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.writeAndFlush(new ServerPeerConnectedMessage());
                            }

                        });
                    }
                });
        Channel connectChannel = bootstrap.connect("127.0.0.1", 18080).sync().channel();
        for(int i = 0;i<30;i++){
            connectChannel.writeAndFlush(new WebSocketMessage("tom","betty","hello"));
        }

    }

}
