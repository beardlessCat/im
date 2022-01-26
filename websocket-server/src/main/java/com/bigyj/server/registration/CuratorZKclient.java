package com.bigyj.server.registration;

import com.bigyj.server.utils.SpringContextUtil;
import org.apache.curator.framework.CuratorFramework;

public class CuratorZKclient {
	private static CuratorFramework singleton = null;
	public static CuratorFramework getSingleton()
	{
		if (null == singleton)
		{
			singleton = SpringContextUtil.getBean(CuratorFramework.class);
		}
		return singleton;
	}
}
