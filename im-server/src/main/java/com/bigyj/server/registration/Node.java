package com.bigyj.server.registration;

import java.io.Serializable;

import lombok.Data;

@Data
public class Node implements Serializable {

	//worker 的Id,zookeeper负责生成
	private long id;

	//Netty 服务 IP
	private String host="127.0.0.1";

	//Netty 服务 端口
	private Integer port=8081;

	public Node(String host, Integer port) {
		this.host = host;
		this.port = port;
	}
}
