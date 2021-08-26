package com.bigyj.server.session;

import com.bigyj.entity.MsgDto;
import com.bigyj.entity.SessionCache;
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
	public boolean writeAndFlush(MsgDto msg) {
		long id = sessionCache.getServerNode().getId();
		ServerPeerSender serverPeerSender = ServerRouterWorker.instance().router(id);
		serverPeerSender.getChannel().writeAndFlush(msg);
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
