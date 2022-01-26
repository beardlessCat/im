package com.bigyj.server.cach;

import com.bigyj.entity.SessionCache;

public interface SessionCacheSupport {
	//保存连接信息
	void save(SessionCache s);

	//获取连接信息
	SessionCache get(String userId);

	//删除连接信息
	void remove(String userId);

}
