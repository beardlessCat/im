package com.bigyj.server.session;

import com.bigyj.entity.MsgDto;
import com.bigyj.entity.SessionCache;
import lombok.Data;

@Data
public class RemoteSession implements ServerSession {
	private SessionCache sessionCache;

	public RemoteSession(SessionCache sessionCache) {
		this.sessionCache = sessionCache;
	}

	@Override
	public boolean writeAndFlush(MsgDto pkg) {
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
