package com.bigyj.server.holder;

import java.util.concurrent.ConcurrentHashMap;

import com.bigyj.server.worker.ServerRouterWorker;

/**
 * 服务器间链接管理holder
 */
public class ServerPeerSenderHolder {
	private ConcurrentHashMap<String, ServerRouterWorker> serverSenders =
			new ConcurrentHashMap<>();

	public void addWorker(String key,ServerRouterWorker serverRouterWorker){
		serverSenders.putIfAbsent(key,serverRouterWorker);
	}

	public ServerRouterWorker getWorker(String key){
		return serverSenders.get(key);
	}

	public void removeWorker(String key){
		serverSenders.remove(key);
	}
}
