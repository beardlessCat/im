package com.bigyj.client;

import com.bigyj.client.client.ImClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClientApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ClientApplication.class, args);
        startClient(context);
    }

    private static void startClient(ConfigurableApplicationContext context) {
        ImClient imClient = context.getBean(ImClient.class);
        imClient.doConnect();
    }
}
