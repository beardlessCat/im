package com.bigyj.client.load.balance;

import java.util.List;

import com.bigyj.entity.Node;

public abstract class AbstractLoadBalance implements LoadBalance{
	@Override
	public Node selectNode(List<Node> nodes) {
		if(nodes.isEmpty()){
			return null;
		}
		if(nodes.size() == 1){
			return nodes.get(0);
		}
		return doSelect(nodes);
	}

	protected abstract Node doSelect(List<Node> nodes);
}
