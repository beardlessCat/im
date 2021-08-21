package com.bigyj.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class ServerNode implements Serializable {

	//worker 的Id,zookeeper负责生成
	private long id;

	//Netty 服务 IP
	private String host;

	//Netty 服务 端口
	private Integer port;

	//权重（客户端连接数量）
	private Integer weight = 0;

	public ServerNode(String host, Integer port) {
		this.host = host;
		this.port = port;
	}
}
