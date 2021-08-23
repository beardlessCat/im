package com.bigyj.server.session;

public class RemoteSession implements ServerSession {
	@Override
	public void writeAndFlush(Object pkg) {

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
