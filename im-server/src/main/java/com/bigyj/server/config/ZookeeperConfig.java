package com.bigyj.server.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfig {
	@Value("${zk.url}")
	private String zkUrl;

	@Bean("curatorFramework")
	public CuratorFramework curatorFramework(){
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
		CuratorFramework client = CuratorFrameworkFactory.newClient(zkUrl,retryPolicy);
		client.start();
		return client;
	}
}
