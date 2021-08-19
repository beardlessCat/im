package com.bigyj.server.registration;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ZkServiceImpl implements ZkService {
	@Autowired
	CuratorFramework curatorFramework;


	@Override
	public boolean checkNodeExists(String path) throws Exception {
		Stat stat = curatorFramework.checkExists().forPath(path);
		return stat==null?false:true;
	}

	@Override
	public String createPersistentNode(String path) throws Exception {
		String pathRegistered = curatorFramework.create()
				.creatingParentsIfNeeded()
				.withProtection()
				.withMode(CreateMode.PERSISTENT)
				.forPath(path);
		return pathRegistered;
	}

	@Override
	public String createNode(String prefix,Node node) throws Exception {
		byte[] payload = new Gson().toJson(node).getBytes(StandardCharsets.UTF_8);
		String pathRegistered = curatorFramework.create()
				.creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
				.forPath(prefix, payload);
		return pathRegistered;
	}
}
