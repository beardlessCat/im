package com.bigyj.server.worker;

import com.bigyj.entity.ServerNode;
import com.bigyj.server.utils.SpringContextUtil;
import lombok.Data;

/**
 * 服务端zookeeper协调者（zookeepr协调客户端）
 */
@Data
public class ServerWorker {
	private ServerNode serverNode ;
	/**
	 *饿汉式创建ServerWorker单例
	 */
	private static final ServerWorker instance = new ServerWorker();
	public static ServerWorker instance(){
		ServerRouterWorker bean = SpringContextUtil.getBean(ServerRouterWorker.class);
		return instance;
	}

}
