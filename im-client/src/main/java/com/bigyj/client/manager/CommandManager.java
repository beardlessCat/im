package com.bigyj.client.manager;

import com.bigyj.client.client.ClientSession;
import com.bigyj.client.client.ImClient;
import com.bigyj.client.sender.ChatMsgSender;
import com.bigyj.client.sender.LoginMsgSender;
import com.bigyj.entity.Msg;
import com.bigyj.entity.User;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service("commandManager")
@Slf4j
@Data
public class CommandManager {
    @Autowired
    private ImClient imClient;
    @Autowired
    private ChatMsgSender chatMsgSender ;
    @Autowired
    private LoginMsgSender loginMsgSender ;

    private ClientSession session;

    public void startClient() {
        Channel channel = imClient.doConnect();
        session = new ClientSession(channel);
        this.startCommand();
    }

    private void startCommand() {
        logger.error("请输入相关命令：1-登录；2-开始聊天；3-退出登录");
        while (true){
            Scanner scanner = new Scanner(System.in);
            String key = scanner.next();
            if("1".equals(key)){
                logger.error("开始登录，请输入用户名及密码");
                User user = new User();
                user.setUid("username");
                user.setToken("123456");
                user.setDevId("1111");
                user.setPlatform(1);
                session.setUser(user);
                loginMsgSender.setUser(user);
                loginMsgSender.setSession(session);
                Msg msg = new Msg();
                msg.setUser(user);
                msg.setMsgType(Msg.MsgType.LOGIN);
                loginMsgSender.sendMsg(msg);
            }else if("2".equals(key)) {
                logger.error("开始聊天，请输入聊天内容");
                while (true) {
                    String msg = scanner.next();
                    //chatMsgSender.sendMsg(msg);
                }
            }else if("3".equals(key)){
                logger.error("您已退出！");
            }else {
                logger.error("无法识别指令[{}]，请重新输入!",key);
            }
        }
    }
}