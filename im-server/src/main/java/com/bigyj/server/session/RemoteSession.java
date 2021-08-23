package com.bigyj.server.session;

public class RemoteSession implements Session{
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
