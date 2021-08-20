package com.bigyj.server.registration;

import com.bigyj.entity.Node;

public interface ZkService {
	boolean checkNodeExists(String path) throws Exception;

	String createPersistentNode(String path) throws Exception;

	String createNode(String prefix , Node node) throws Exception;
}
