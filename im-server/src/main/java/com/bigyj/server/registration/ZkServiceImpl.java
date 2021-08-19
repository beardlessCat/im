package com.bigyj.server.registration;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ZkServiceImpl implements ZkService {
	@Autowired
	CuratorFramework curatorFramework;

	public boolean create(String path){
		try
		{
			curatorFramework.create().forPath(path);
			return true;
		}catch (Exception ex)
		{
			logger.error("【创建节点失败】{},{}",path,ex);
			return false;
		}
	}

	public boolean create(String path, byte[] payload){
		try
		{
			curatorFramework.create().forPath(path, payload);
			return true;
		}catch (Exception ex)
		{
			logger.error("【创建节点失败】{},{}",path,ex);
			return false;
		}
	}

	public boolean createEphemeral(String path, byte[] payload) {
		try
		{
			curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(path, payload);
			return true;
		}catch (Exception ex)
		{
			logger.error("【创建节点失败】{},{}",path,ex);
			return false;
		}
	}

	public String createEphemeralSequential(CuratorFramework client, String path, byte[] payload) {
		try
		{
			return client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, payload);
		}
		catch (Exception ex)
		{
			logger.error("【创建节点存在异常】{},{}",path,ex);
			return null;
		}
	}

	public Stat Exists(String path){
		try
		{
			Stat stat = curatorFramework.checkExists().forPath(path);
			return stat;
		}
		catch (Exception ex)
		{
			logger.error("【查看指定节点是否存在异常】{},{}",path,ex);
			return null;
		}
	}

	public String getData(String path) {
		try
		{
			return new String(curatorFramework.getData().forPath(path));
		}
		catch (Exception ex)
		{
			logger.error("【获取节点数据异常】{},{}",path,ex);
			return null;
		}

	}

	public boolean setData(String path){
		try {
			curatorFramework.setData().forPath(path);
			return true;
		}
		catch (Exception ex)
		{
			logger.error("【设置节点值异常】{},{}",path,ex);
			return false;
		}
	}

	public boolean setDataAsync(String path, byte[] payload) {
		try {
			CuratorListener listener = new CuratorListener() {
				@Override
				public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
					if (event != null)
						System.out.println(event.toString());
				}
			};
			curatorFramework.getCuratorListenable().addListener(listener);

			curatorFramework.setData().inBackground().forPath(path, payload);

			return true;
		}
		catch (Exception ex)
		{
			logger.error("【设置节点值异常】{},{}",path,ex);
			return false;
		}

	}

	public boolean setDataAsyncWithCallback(BackgroundCallback callback, String path, byte[] payload){
		try
		{
			curatorFramework.setData().inBackground(callback).forPath(path, payload);
			return true;
		}
		catch (Exception ex)
		{
			logger.error("【设置节点值异常】{},{}",path,ex);
			return false;
		}
	}

	public List<String> getChildren(String path) {
		try
		{
			return curatorFramework.getChildren().forPath(path);
		}
		catch (Exception ex)
		{
			logger.error("【查询指定子节点异常】{},{}",path,ex);
			return null;
		}
	}

	public List<String> watchedGetChildren(CuratorFramework client, String path) {
		try
		{
			return client.getChildren().watched().forPath(path);
		}
		catch (Exception ex)
		{
			logger.error("【查询指定子节点异常】{},{}",path,ex);
			return null;
		}
	}

	public List<String> watchedGetChildren(CuratorFramework client, String path, Watcher watcher){
		try
		{
			return client.getChildren().usingWatcher(watcher).forPath(path);
		}
		catch (Exception ex)
		{
			logger.error("【查询指定子节点异常】{},{}",path,ex);
			return null;
		}
	}

	public boolean delete(CuratorFramework client, String path){
		try
		{
			client.delete().forPath(path);
			return true;
		}
		catch (Exception ex)
		{
			logger.error("【删除指定节点异常】{},{}",path,ex);
			return false;
		}
	}

	public boolean guaranteedDelete(CuratorFramework client, String path) {
		try {
			client.delete().guaranteed().forPath(path);
			return true;
		}
		catch (Exception ex) {
			logger.error("【删除指定节点异常】{},{}", path, ex);
			return false;
		}
	}
}
