package com.bigyj.server;

import com.bigyj.server.server.WebsocketServer;

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
        WebsocketServer websocketServer =context.getBean(WebsocketServer.class);
        websocketServer.startImServer();
    }
}
