package com.bigyj.entity;

import lombok.Data;

@Data
public class ClientNode {
	//Netty 客户端 IP
	private String host;

	//Netty 客户端 端口
	private Integer port;

	ClientNode(String host, Integer port) {
		this.host = host;
		this.port = port;
	}
}
