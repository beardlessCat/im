package com.bigyj.server.registration;

public interface ZkService {
	public boolean checkNodeExists(String path) throws Exception;

	public String createPersistentNode(String path) throws Exception;

	public String createNode(String prefix ,Node node) throws Exception;
}
