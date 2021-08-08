package com.bigyj.server;

import com.bigyj.server.server.ImServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);
        startChatServer(context);
    }

    /**
     * 启动服务器
     * @param context
     */
    private static void startChatServer(ConfigurableApplicationContext context) {
        ImServer imServer =context.getBean(ImServer.class);
        imServer.startImServer();
    }
}
