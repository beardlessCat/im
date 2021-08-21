package com.bigyj.client.load.balance;

import java.util.List;

import com.bigyj.entity.Node;

/**
 * 一致性HASH
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance{
	@Override
	protected Node doSelect(List<Node> nodes) {
		return null;
	}
}
