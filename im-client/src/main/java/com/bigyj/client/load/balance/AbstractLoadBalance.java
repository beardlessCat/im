package com.bigyj.client.load.balance;

import java.util.List;

import com.bigyj.entity.ServerNode;

public abstract class AbstractLoadBalance implements LoadBalance{
	@Override
	public ServerNode selectNode(List<ServerNode> serverNodes) {
		if(serverNodes.isEmpty()){
			return null;
		}
		if(serverNodes.size() == 1){
			return serverNodes.get(0);
		}
		return doSelect(serverNodes);
	}

	protected abstract ServerNode doSelect(List<ServerNode> serverNodes);
}
