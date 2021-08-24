package com.bigyj.server.manager;

import com.bigyj.entity.ServerNode;
import com.bigyj.entity.SessionCache;
import com.bigyj.server.cach.SessionCacheSupport;
import com.bigyj.server.holder.LocalSessionHolder;
import com.bigyj.server.worker.ServerWorker;
import com.bigyj.server.session.LocalSession;
import com.bigyj.server.session.RemoteSession;
import com.bigyj.server.session.ServerSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerSessionManager {
	@Autowired
	private SessionCacheSupport sessionCacheSupport;
	/**
	 * 根据userid获取session
	 * @param userId
	 * @return
	 */
	public ServerSession getServerSession(String userId){
		ServerSession serverSession = null;
		SessionCache sessionCache = sessionCacheSupport.get(userId);
		//redis中查询不到相关客户端
		if(sessionCache ==null){
			return null;
		}
		//判断消息接受者是不是连接当前服务
		ServerNode cacheServerNode = sessionCache.getServerNode();
		ServerNode serverNode = ServerWorker.instance().getServerNode();
		if(serverNode.getAddress().equals(cacheServerNode.getAddress())){
			//当前服务
			serverSession = LocalSessionHolder.getServerSession(userId);
		}else {
			//远程服务
			serverSession = new RemoteSession(sessionCache);
		}
		return serverSession;
	}

	/**
	 * 保存servreSession
	 * @param localSession
	 */
	public void addServerSession(LocalSession localSession){
		//添加至本地session
		LocalSessionHolder.addServerSession(localSession);
		//存储至redis数据库中
		String sessionId = localSession.getSessionId();
		String userId = localSession.getUserId();
		ServerNode serverNode = ServerWorker.instance().getServerNode();
		SessionCache sessionCache = new SessionCache(sessionId,userId,serverNode) ;
		sessionCacheSupport.save(sessionCache);
	}

}
