package com.bigyj.server.session;

import com.bigyj.entity.SessionCache;
import com.bigyj.message.ChatRequestMessage;
import com.bigyj.server.server.ServerPeerSender;
import com.bigyj.server.worker.ServerRouterWorker;
import lombok.Data;

@Data
public class RemoteSession implements ServerSession {
	private SessionCache sessionCache;

	public RemoteSession(SessionCache sessionCache) {
		this.sessionCache = sessionCache;
	}

	@Override
	public boolean writeAndFlush(ChatRequestMessage chatRequestMessage) {
		//获取对方用户所在的服务器，将消息转发至对方服务器（该服务器作为客户端）。
		long id = sessionCache.getServerNode().getId();
		ServerPeerSender serverPeerSender = ServerRouterWorker.instance().router(id);
		serverPeerSender.getChannel().writeAndFlush(chatRequestMessage);
		return true;
	}

	@Override
	public String getSessionId() {
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public String getUserId() {
		return null;
	}
}
