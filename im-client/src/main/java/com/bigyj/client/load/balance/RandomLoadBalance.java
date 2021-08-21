package com.bigyj.client.load.balance;

import java.util.List;

import com.bigyj.entity.Node;

/**
 *
 */
public class RandomLoadBalance extends AbstractLoadBalance{
	@Override
	protected Node doSelect(List<Node> nodes) {
		return null;
	}
}
