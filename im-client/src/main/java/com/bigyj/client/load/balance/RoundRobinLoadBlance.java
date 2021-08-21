package com.bigyj.client.load.balance;

import java.util.List;

import com.bigyj.entity.ServerNode;

/**
 *轮询调用
 */
public class RoundRobinLoadBlance extends AbstractLoadBalance{

	@Override
	protected ServerNode doSelect(List<ServerNode> serverNodes) {
		return null;
	}
}
