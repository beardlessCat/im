package com.bigyj.client.load.balance;

import java.util.List;

import com.bigyj.entity.ServerNode;
public interface LoadBalance {
	ServerNode selectNode(List<ServerNode> serverNodes);
}
