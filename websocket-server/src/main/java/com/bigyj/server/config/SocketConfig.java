package com.bigyj.server.config;

import lombok.Data;

@Data
public class SocketConfig {

    private boolean tcpNoDelay = true;

    private int tcpSendBufferSize = -1;

    private int tcpReceiveBufferSize = -1;

    private boolean tcpKeepAlive = false;

    private int soLinger = -1;

    private boolean reuseAddress = false;

    private int acceptBackLog = 1024;

}
