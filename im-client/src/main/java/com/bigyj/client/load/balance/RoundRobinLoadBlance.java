package com.bigyj.client.load.balance;

import com.bigyj.entity.ServerNode;
import com.bigyj.entity.WeightedRoundRobin;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *轮询调用
 */
@Slf4j
public class RoundRobinLoadBlance extends AbstractLoadBalance{

	private static final String CLIENT_KEY = "IM_CLIENT" ;
	Map<String, ConcurrentHashMap<String,WeightedRoundRobin>> nodes = new ConcurrentHashMap<>();
	@Override
	protected ServerNode doSelect(List<ServerNode> serverNodes) {
		ConcurrentHashMap<String, WeightedRoundRobin> map = nodes.computeIfAbsent(CLIENT_KEY, key ->new ConcurrentHashMap<>());
		long maxValue =Long.MIN_VALUE;
		long now = System.currentTimeMillis();
		int total = 0;
		ServerNode selectedServerNode = null;
		WeightedRoundRobin selectedWRR =null;
		for(ServerNode serverNode:serverNodes){
			String serverKey = serverNode.toString();
			Integer weight = serverNode.getWeight();
			WeightedRoundRobin weightObject = map.computeIfAbsent(serverKey, key -> {
				WeightedRoundRobin weightedRoundRobin = new WeightedRoundRobin();
				weightedRoundRobin.setWeight(weight);
				weightedRoundRobin.setLastUpdate(now);
				return weightedRoundRobin;
			});
			//weight changed
			if (weight != weightObject.getWeight()) {
				weightObject.setWeight(weight);
			}
			long nextWeight = weightObject.increaseCurrent();
			//判断加上权重是否大于当前的最大值，
			if(nextWeight > maxValue){
				selectedServerNode = serverNode;
				selectedWRR = weightObject;
				maxValue = nextWeight;
			}
			total+=weight;
		};
		selectedWRR.sel(total);
		if(selectedServerNode!=null){
			logger.info("选中节点"+selectedServerNode.toString());
		}
		return selectedServerNode;
	}
}
