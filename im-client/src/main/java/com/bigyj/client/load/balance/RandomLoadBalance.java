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
		int size = serverNodes.size();
		int[] weights = new int[size];
		int total = 0 ;
		for(int i=0;i<size;i++){
			Integer weight = serverNodes.get(i).getWeight();
			total+=weight;
			weights[i] = total;
		}
		//节点权重全部为0，随机选择一个节点 fix #3  https://github.com/beardlessCat/im/issues/3
		if (total==0){
			//select a node randomly
			return serverNodes.get(ThreadLocalRandom.current().nextInt(serverNodes.size()));
		}
		//类似于抽奖算法
		int randomIndex = ThreadLocalRandom.current().nextInt(weights[size - 1]);
		for(int index = 0;index<size;index++){
			if(randomIndex < weights[index]){
				return serverNodes.get(index);
			}
		}
		//兜底
		return serverNodes.get(ThreadLocalRandom.current().nextInt(serverNodes.size()));
	}
}
