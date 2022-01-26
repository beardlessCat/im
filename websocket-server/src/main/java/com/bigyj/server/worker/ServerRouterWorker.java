package com.bigyj.server.worker;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.ServerNode;
import com.bigyj.server.holder.ServerPeerSenderHolder;
import com.bigyj.server.registration.CuratorZKclient;
import com.bigyj.server.server.ServerPeerSender;
import com.bigyj.utils.NodeUtil;
import com.bigyj.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

@Slf4j
public class ServerRouterWorker {

	private static final String MANAGE_PATH ="/websocket/nodes";
	public static final String PATH_PREFIX = MANAGE_PATH + "/seq-";

	private static final ServerRouterWorker instance = new ServerRouterWorker();
	public static ServerRouterWorker instance(){
		return instance;
	}

	private boolean inited = false;

	public void init() throws Exception {
		if(inited){
			return;
		}
		CuratorFramework curatorFramework = CuratorZKclient.getSingleton();
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
						processAdd(data);
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

	/**
	 * zk节点新增
	 * @param data
	 */
	private void processAdd(ChildData data) {
		ServerPeerSender serverPeerSender = new ServerPeerSender();
		long id = NodeUtil.getIdByPath(data.getPath(), PATH_PREFIX);
		ServerNode serverNode = JSONObject.parseObject(data.getData(), ServerNode.class);
		serverNode.setId(id);
		if(ServerWorker.instance().getServerNode().getAddress().equals(serverNode.getAddress())){
			logger.info("监听到自身节点加入，无需进行连接!");
			return;
		}
		serverPeerSender.doConnectedServer(serverNode);
		logger.info("新节点加入：{}",serverNode);
		ServerPeerSenderHolder.addWorker(id,serverPeerSender);
	}

	/**
	 * 路由到某个节点
	 * @param id
	 * @return
	 */
	public ServerPeerSender router(long id) {
		ServerPeerSender worker = ServerPeerSenderHolder.getWorker(id);
		return worker;
	}
}
