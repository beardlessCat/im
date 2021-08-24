package com.bigyj.server;

import com.bigyj.server.server.ImServer;
import com.bigyj.server.worker.ServerRouterWorker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);
        startChatServer(context);
    }

    /**
     * 启动服务器
     * @param context
     */
    private static void startChatServer(ConfigurableApplicationContext context) throws Exception {
        ImServer imServer =context.getBean(ImServer.class);
        imServer.startImServer();
    }
}
