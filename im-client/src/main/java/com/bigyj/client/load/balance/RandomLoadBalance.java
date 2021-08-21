package com.bigyj.client.load.balance;

import java.util.List;
import java.util.Random;
import com.bigyj.entity.ServerNode;

/**
 *随机调用负载均衡
 */
public class RandomLoadBalance extends AbstractLoadBalance{
	private final Random random = new Random();
	@Override
	protected ServerNode doSelect(List<ServerNode> serverNodes) {
		//权重是否一致 fixme
		int randomIndex = random.nextInt(serverNodes.size());
		return serverNodes.get(randomIndex);
	}
}
