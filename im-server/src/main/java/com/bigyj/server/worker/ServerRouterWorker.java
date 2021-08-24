package com.bigyj.server.worker;

import com.bigyj.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerRouterWorker {
	@Autowired
	private CuratorFramework curatorFramework;

	private static final String MANAGE_PATH ="/im/nodes";

	private boolean inited = false;

	public void init() throws Exception {
		if(inited){
			return;
		}
		//订阅节点的增加和删除事件
		PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, MANAGE_PATH, true);
		PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client,
					PathChildrenCacheEvent event) throws Exception {
				ChildData data = event.getData();
				switch (event.getType()) {
					case CHILD_ADDED:
						logger.info("CHILD_ADDED : " + data.getPath());
						break;
					case CHILD_REMOVED:
						logger.info("CHILD_REMOVED : " + data.getPath());
						break;
					case CHILD_UPDATED:
						logger.info("CHILD_UPDATED : " + data.getPath());
						break;
					default:
						logger.debug("[PathChildrenCache]节点数据为空, path={}", data == null ? "null" : data.getPath());
						break;
					}
			}
		};
		childrenCache.getListenable().addListener(
				childrenCacheListener, ThreadUtils.getExecutor());
		logger.info("Register zk watcher successfully!");
		childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
		this.inited = true ;
	}
}
