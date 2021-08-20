package com.bigyj.client.zk;

import java.util.List;

import com.bigyj.entity.Node;

public interface ZkService {
	List<Node> getWorkers(String path,String prefix);
}
