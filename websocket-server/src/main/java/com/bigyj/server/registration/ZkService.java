package com.bigyj.server.registration;

import com.bigyj.entity.ServerNode;

public interface ZkService {
	boolean checkNodeExists(String path) throws Exception;

	String createPersistentNode(String path) throws Exception;

	String createNode(String prefix , ServerNode serverNode) throws Exception;
}
