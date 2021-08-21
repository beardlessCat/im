package com.bigyj.client.load.balance;

import java.util.List;

import com.bigyj.entity.ServerNode;

/**
 * 最少活跃数
 */
public class LeastActiveLoadBlance extends AbstractLoadBalance{

	@Override
	protected ServerNode doSelect(List<ServerNode> serverNodes) {
		return null;
	}
}
