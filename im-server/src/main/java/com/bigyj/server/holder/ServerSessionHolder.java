package com.bigyj.server.holder;

import java.util.concurrent.ConcurrentHashMap;

import com.bigyj.server.server.ServerSession;

public class ServerSessionHolder {

	private static ConcurrentHashMap<String, ServerSession> serverSessions
			= new ConcurrentHashMap<>();

	public static void addServerSession(ServerSession serverSession){
		serverSessions.putIfAbsent(serverSession.getUser().getUid(),serverSession);
	}

	public static ServerSession getServerSession(String userId){
		return serverSessions.get(userId);
	}
}
