package com.bigyj.client.zk;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.Node;
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
	public List<Node> getWorkers(String path,String prefix) {
		List<Node> workers = new ArrayList<Node>();
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
			Node node = JSONObject.parseObject(payload, Node.class);
			node.setId(NodeUtil.getIdByPath(child,prefix));
			workers.add(node);
		}
		return workers;
	}
}
