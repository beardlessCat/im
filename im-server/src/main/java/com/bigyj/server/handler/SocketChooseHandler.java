package com.bigyj.server.handler;

import com.bigyj.protocol.ChatMessageCodec;
import com.bigyj.protocol.ProtocolFrameDecoder;
import com.bigyj.server.handler.socket.ServerPeerConnectedHandler;
import com.bigyj.server.handler.websocket.SecurityServerHandler;
import com.bigyj.server.handler.websocket.TextWebSocketFrameHandler;
import com.bigyj.server.handler.websocket.WebsocketServerFinishHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
public class SocketChooseHandler extends ByteToMessageDecoder {

    /** 默认暗号长度为23 */
    private static final int MAX_LENGTH = 23;
    /** WebSocket握手的协议前缀 */
    private static final String WEBSOCKET_PREFIX = "GET /";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String protocol = getBufStart(in);
        ChannelPipeline pipeline = ctx.pipeline();
        if (protocol.startsWith(WEBSOCKET_PREFIX)) {
            logger.info("webSocket协议");
            //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
            pipeline.addLast(new HttpServerCodec());
            //以块的方式来写的处理器
            pipeline.addLast(new ChunkedWriteHandler());
            //netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
            pipeline.addLast(new HttpObjectAggregator(8192));
            //初始化http
            pipeline.addLast(new SecurityServerHandler());
            //ws://localhost:9999/ws
            //参数指的是contex_path
            pipeline.addLast(new WebSocketServerProtocolHandler("/ws",null,true,65546,false,true));
            //握手成功
            pipeline.addLast(new WebsocketServerFinishHandler());
            //websocket定义了传递数据的6中frame类型
            pipeline.addLast(new TextWebSocketFrameHandler());
            //移除当前handler
            pipeline.remove("socketChooseHandler");
        }else {
            logger.info("tcpSocket协议");
            //处理粘包与半包
            pipeline.addLast(new ProtocolFrameDecoder());
            //加入新的协议编码与界面器
            pipeline.addLast(new ChatMessageCodec());
            pipeline.addLast(new ServerPeerConnectedHandler());
        }
    }

    private String getBufStart(ByteBuf in){
        int length = in.readableBytes();
        if (length > MAX_LENGTH) {
            length = MAX_LENGTH;
        }
        // 标记读位置
        in.markReaderIndex();
        byte[] content = new byte[length];
        in.readBytes(content);
        //一定要重新重置
        in.resetReaderIndex();
        return new String(content);
    }
}
