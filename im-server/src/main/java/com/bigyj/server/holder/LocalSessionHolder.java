package com.bigyj.server.holder;

import java.util.concurrent.ConcurrentHashMap;

import com.bigyj.server.session.LocalSession;

public class LocalSessionHolder {

	private static ConcurrentHashMap<String, LocalSession> serverSessions
			= new ConcurrentHashMap<>();

	public static void addServerSession(LocalSession serverSession){
		serverSessions.putIfAbsent(serverSession.getUser().getUid(),serverSession);
	}

	public static LocalSession getServerSession(String userId){
		return serverSessions.get(userId);
	}

	public static LocalSession removeServerSession(String userId){
		return serverSessions.remove(userId);
	}


}
