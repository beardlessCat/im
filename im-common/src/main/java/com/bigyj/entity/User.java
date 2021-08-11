package com.bigyj.entity;

import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
@Data
public class User {
    private static final AtomicInteger NO=new AtomicInteger(1);
    String uid = String.valueOf(NO.getAndIncrement());
    String devId= UUID.randomUUID().toString();
    String token= UUID.randomUUID().toString();
    String nickName = "nickName";
    private int platform;
    private String sessionId;

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", devId='" + devId + '\'' +
                ", token='" + token + '\'' +
                ", nickName='" + nickName + '\'' +
                ", platform=" + platform +
                '}';
    }

}
