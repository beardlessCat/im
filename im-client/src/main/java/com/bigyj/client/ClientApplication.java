package com.bigyj.client;

import com.bigyj.client.manager.CommandManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClientApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ClientApplication.class, args);
        //服务启动成功后开启客户端
        startClient(context);
    }

    private static void startClient(ConfigurableApplicationContext context) {
        CommandManager imClient = context.getBean(CommandManager.class);
        imClient.startClient();
    }
}
