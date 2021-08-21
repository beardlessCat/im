package com.bigyj.client.zk;

import java.util.List;

import com.bigyj.entity.ServerNode;

public interface ZkService {
	List<ServerNode> getWorkers(String path,String prefix);
}
