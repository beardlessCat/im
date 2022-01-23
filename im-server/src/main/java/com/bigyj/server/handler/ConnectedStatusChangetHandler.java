package com.bigyj.server.handler;

import com.bigyj.server.holder.LocalSessionHolder;
import com.bigyj.server.session.LocalSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class ConnectedStatusChangetHandler extends ChannelInboundHandlerAdapter {
	public static final AttributeKey<LocalSession> SESSION_KEY =
			AttributeKey.valueOf("SESSION_KEY");
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("================新的连接建立=================");
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.warn("=================连接断开====================");
		LocalSession localSession = ctx.channel().attr(SESSION_KEY).get();
		String uid = localSession.getUser().getUid();
		LocalSessionHolder.removeServerSession(uid);
		super.channelInactive(ctx);
	}
}
