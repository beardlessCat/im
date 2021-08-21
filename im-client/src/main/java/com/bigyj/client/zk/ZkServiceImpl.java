package com.bigyj.client.zk;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.ServerNode;
import com.bigyj.utils.NodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ZkServiceImpl implements ZkService {
	@Autowired
	CuratorFramework curatorFramework;

	@Override
	public List<ServerNode> getWorkers(String path,String prefix) {
		List<ServerNode> workers = new ArrayList<ServerNode>();
		List<String> children = null;
		try
		{
			children = curatorFramework.getChildren().forPath(path);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

		for (String child : children)
		{
			logger.info("child:", child);
			byte[] payload = null;
			try
			{
				payload = curatorFramework.getData().forPath(path + "/" + child);

			} catch (Exception e)
			{
				e.printStackTrace();
			}
			if (null == payload)
			{
				continue;
			}
			ServerNode serverNode = JSONObject.parseObject(payload, ServerNode.class);
			serverNode.setId(NodeUtil.getIdByPath(child,prefix));
			workers.add(serverNode);
		}
		return workers;
	}
}
