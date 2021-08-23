package com.bigyj.server.session;

public class ServerSessionManager {
	/**
	 *饿汉式创建ServerSessionManager单例
	 */
	private static final ServerSessionManager singleInstance = new ServerSessionManager();
	//取得单例
	public synchronized static ServerSessionManager instance() {
		return singleInstance;
	}

	/**
	 * 根据userid获取session
	 * @param userId
	 * @return
	 */
	public ServerSession getServerSession(String userId){

		return null;
	}

	public ServerSession addServerSession(){
		//添加至本地session

		//存储至redis数据库中
		return null;
	}
}
