package com.bigyj.server.server;

import com.bigyj.entity.ServerNode;
import com.bigyj.server.handler.client.ServerBeatHandler;
import com.bigyj.server.handler.client.ServerExceptionHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.Data;

/**
 * 服务器间连接建立
 */
@Data
public class ServerPeerSender {
	private Channel channel ;
	private Bootstrap bootstrap;
	private EventLoopGroup eventLoopGroup;

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
						pipeline.addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
						pipeline.addLast("encoder",new StringEncoder(CharsetUtil.UTF_8));
						//服务端之间的心跳
						pipeline.addLast("serverBeat",new ServerBeatHandler());
						//服务间的重连
						pipeline.addLast("serverException",new ServerExceptionHandler());
					}
				});
			channel = bootstrap.connect(serverNode.getHost(), serverNode.getPort()).sync().channel();
			this.channel = channel ;
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
