package com.bigyj.server.manager;

import com.bigyj.user.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class MemoryUserManager {
    private static Map<String, User> users = new ConcurrentHashMap<>();
    static {
        users.put("tom",new User("1","123","tom"));
        users.put("jerry",new User("2","123","jerry"));
        users.put("betty",new User("3","123","betty"));
        users.put("cook",new User("4","123","cook"));
        users.put("server",new User("server","123","server"));

    }
    public static User getUserByName(String userName) {
        return users.get(userName);
    }
}
