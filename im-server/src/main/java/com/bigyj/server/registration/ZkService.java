package com.bigyj.server.registration;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

public interface ZkService {
	boolean create(String path);

	/**
	 * 给指定的ZNode设置值，不能嵌套创建目录
	 * 如果节点已经存在，会抛异常org.apache.zookeeper.KeeperException$NodeExistsException: KeeperErrorCode = NodeExists for /path
	 */
	boolean create(String path, byte[] payload);

	/**
	 * 创建临时节点
	 * 需要注意：虽说临时节点是session失效后立刻删除，但是并不是client一断开连接，session就立刻会失效
	 * 失效是由zk服务端控制的，每次连接时，客户端和服务端会协商一个合理的超时时间
	 * 如果超过了超时时间client都一直美哦与发送心跳，才回真的删除这个session创建的临时节点
	 */
	boolean createEphemeral(String path, byte[] payload);


	String createEphemeralSequential(CuratorFramework client, String path, byte[] payload) throws Exception;

	/**
	 * 检查某个节点是否存在
	 */
	Stat Exists(String path);

	/**
	 * 获取节点数据
	 */
	String getData(String path);

	/*
	 *
	 * 设置节点数据
	 */
	boolean setData(String path);

	boolean setDataAsync(String path, byte[] payload);

	boolean setDataAsyncWithCallback(BackgroundCallback callback, String path, byte[] payload);

	/**
	 * 查询子节点
	 */
	List<String> getChildren(String path);

	List<String> watchedGetChildren(CuratorFramework client, String path);

	List<String> watchedGetChildren(CuratorFramework client, String path, Watcher watcher);

	/**
	 * 删除节点
	 */
	boolean delete(CuratorFramework client, String path);

	/**
	 * 删除一个节点，强制保证删除
	 */
	boolean guaranteedDelete(CuratorFramework client, String path);
}
