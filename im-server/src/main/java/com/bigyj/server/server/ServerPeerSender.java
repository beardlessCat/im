package com.bigyj.server.server;

import com.bigyj.entity.ServerNode;
import com.bigyj.message.ChatRequestMessage;
import com.bigyj.message.LoginRequestMessage;
import com.bigyj.message.PingMessage;
import com.bigyj.message.ServerPeerConnectedMessage;
import com.bigyj.protocol.ChatMessageCodec;
import com.bigyj.protocol.ProtocolFrameDecoder;
import com.bigyj.server.worker.ServerWorker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 作为客户点连接其他服务（服务器间连接建立）
 */
@Data
@Slf4j
public class ServerPeerSender {
	private Channel channel ;
	private Bootstrap bootstrap;
	private EventLoopGroup eventLoopGroup;
	private static final int WRITE_IDLE_GAP = 150;

	public ServerPeerSender() {
		bootstrap = new Bootstrap();
		eventLoopGroup = new NioEventLoopGroup();
	}
	public void doConnectedServer(ServerNode serverNode){
		try {
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
									logger.debug("{} 没有写数据了，发送一个心跳包[服务间]",WRITE_IDLE_GAP);
									ctx.writeAndFlush(new PingMessage());
								}
							}
							//服务端认证
							@Override
							public void channelActive(ChannelHandlerContext ctx) throws Exception {
								ctx.writeAndFlush(new ServerPeerConnectedMessage());
							}
						});
						//服务间的重连 fixme
//						pipeline.addLast("serverException",new ServerExceptionHandler());
					}
				});
			Channel connectChannel = bootstrap.connect(serverNode.getHost(), serverNode.getPort()).sync().channel();
			//增加是连接成功监听、连接重试机制及连接关闭监听
			this.channel = connectChannel;
			logger.info("服务端{}作为客户端，加入{}成功",
					ServerWorker.instance().getServerNode().getAddress(),
					serverNode.getAddress());
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
