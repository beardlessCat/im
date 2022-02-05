package com.bigyj.server.config;

import lombok.Data;

@Data
public class NettyServerConfig {
    private boolean useEpollNativeSelector = true ;
    private SocketConfig socketConfig;
    private int bossThreads = 1;
    private int workerThreads ;
}
