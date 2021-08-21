package com.bigyj.client.load.balance;

import java.util.List;

import com.bigyj.entity.Node;

public interface LoadBalance {
	Node selectNode(List<Node> nodes);
}
