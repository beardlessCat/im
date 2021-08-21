package com.bigyj.client.load.balance;

import java.util.List;

import com.bigyj.entity.Node;

/**
 * 最少活跃数
 */
public class LeastActiveLoadBlance extends AbstractLoadBalance{

	@Override
	protected Node doSelect(List<Node> nodes) {
		return null;
	}
}
