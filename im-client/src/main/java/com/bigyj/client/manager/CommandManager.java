package com.bigyj.client.manager;

import com.bigyj.client.client.ImClient;
import com.bigyj.client.sender.ChatMsgSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service("commandManager")
@Slf4j
public class CommandManager {
    @Autowired
    private ImClient imClient;
    @Autowired
    private ChatMsgSender chatMsgSender ;
    public void startClient() {
        imClient.doConnect();
        this.startCommand();
    }

    private void startCommand() {
        logger.error("请输入相关命令：1-登录；2-开始聊天；3-退出登录");
        while (true){
            Scanner scanner = new Scanner(System.in);
            String key = scanner.next();
            if("1".equals(key)){
                logger.error("开始登录，请输入用户名及密码");
            }else if("2".equals(key)) {
                logger.error("开始聊天，请输入聊天内容");
                while (true) {
                    String msg = scanner.next();
                    chatMsgSender.sendMsg(msg);
                }
            }else if("3".equals(key)){
                logger.error("您已退出！");
            }else {
                logger.error("无法识别指令[{}]，请重新输入!",key);
            }
        }
    }
}
