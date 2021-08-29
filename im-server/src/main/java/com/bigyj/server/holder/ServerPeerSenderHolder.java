package com.bigyj.server.holder;

import java.util.concurrent.ConcurrentHashMap;

import com.bigyj.server.server.ServerPeerSender;

/**
 * 服务器间链接管理holder
 */
public class ServerPeerSenderHolder {
	private static ConcurrentHashMap<Long, ServerPeerSender> serverSenders =
			new ConcurrentHashMap<>();

	public static void addWorker(Long key,ServerPeerSender serverPeerSender){
		serverSenders.putIfAbsent(key,serverPeerSender);
	}

	public static ServerPeerSender getWorker(Long key){
		return serverSenders.get(key);
	}

	public static void removeWorker(Long key){
		serverSenders.remove(key);
	}

	public static ConcurrentHashMap<Long, ServerPeerSender> getAll(){
		return serverSenders;
	}

}
