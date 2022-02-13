package com.bigyj.client.load.balance;

import com.bigyj.entity.ServerNode;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *随机调用负载均衡
 */
public class RandomLoadBalance extends AbstractLoadBalance{
	public static final String NAME = "random";
	@Override
	protected ServerNode doSelect(List<ServerNode> serverNodes) {
		//权重是否一致 fixme
		int randomIndex = ThreadLocalRandom.current().nextInt(serverNodes.size());
		return serverNodes.get(randomIndex);
	}
}
