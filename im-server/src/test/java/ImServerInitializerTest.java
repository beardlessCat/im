import com.bigyj.protocol.ChatMessageCodec;
import com.bigyj.protocol.ProtocolFrameDecoder;
import com.bigyj.server.handler.socket.ChatServerRedirectHandler01;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


public class ImServerInitializerTest extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //处理粘包与半包
        pipeline.addLast(new ProtocolFrameDecoder());
        //加入新的协议编码与界面器
        pipeline.addLast(new ChatMessageCodec());
        // ChannelDuplexHandler 可以同时作为入站和出站处理器
        pipeline.addLast(new ChannelDuplexHandler() {
            // 用来触发特殊事件
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                IdleStateEvent event = (IdleStateEvent) evt;
                // 触发了读空闲事件·
                if (event.state() == IdleState.READER_IDLE) {
                    ctx.channel().close();
                }
            }
        });
        //增加聊天的handler
        pipeline.addLast("chat", new ChatServerRedirectHandler01());

    }
}