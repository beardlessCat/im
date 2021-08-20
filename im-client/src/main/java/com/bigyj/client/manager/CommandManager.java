package com.bigyj.client.manager;

import com.bigyj.client.client.ClientSession;
import com.bigyj.client.client.ImClient;
import com.bigyj.client.sender.ChatMsgSender;
import com.bigyj.client.sender.LoginMsgSender;
import com.bigyj.client.sender.LogoutMsgSender;
import com.bigyj.entity.Msg;
import com.bigyj.entity.User;
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
    @Autowired
    private LogoutMsgSender logoutMsgSender ;
    private boolean connectFlag;
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
                boolean login = isLogin();
                if(login){
                    logger.error("您已登录！");
                    continue;
                }
                this.startLogin(scanner);
            }else if("2".equals(key)) {
                boolean login = isLogin();
                if(!login){
                    logger.error("请先登录");
                    continue;
                }
                this.startChat(scanner);
            }else if("3".equals(key)){
                if (!isLogin()) {
                    logger.info("还没有登录，请先登录");
                    continue;
                }
                this.startLogout();
            }else {
                logger.error("无法识别指令[{}]，请重新输入!",key);
            }
        }
    }

    /**
     * 执行退出逻辑
     */
    private void startLogout() {
        User user = session.getUser();
        Msg msg = Msg.builder(Msg.MsgType.LOGOUT_REQUEST, user)
                .build();
        logoutMsgSender.setSession(session);
        logoutMsgSender.sendMsg(msg);
    }

    /**
     * 执行聊天逻辑
     * @param scanner
     */
    private void startChat(Scanner scanner) {
        logger.error("开始聊天，请输入聊天内容(content@userId)");
        while (true) {
            String content = scanner.next();
            if("EXIT".equals(content)){
                this.startLogout();
                continue;
            }
            if(!content.contains("@")){
                logger.error("聊天内容格式为content@userId！");
                continue;
            }
            String[] split = content.split("@");
            String contentValue = split[0];
            String toUserId = split[1];
            User user = session.getUser();
            Msg msg = Msg.builder(Msg.MsgType.CHAT, user)
                    .setToUserId(toUserId)
                    .setContent(contentValue)
                    .build();
            chatMsgSender.setSession(session);
            chatMsgSender.sendMsg(msg);
        }
    }

    /**
     * 执行登录逻辑
     * @param scanner
     */
    private void startLogin(Scanner scanner) {
        while (true) {
            logger.error("开始登录，请输入用户名及token（userName@token）");
            String content = scanner.next();
            if(!content.contains("@")){
                logger.error("登录内容格式为userName@token！");
                continue;
            }
            String[] split = content.split("@");
            String userName = split[0];
            String token = split[1];
            User user = new User();
            user.setUid(userName);
            user.setNickName(userName);
            user.setToken(token);
            session.setUser(user);
            loginMsgSender.setUser(user);
            loginMsgSender.setSession(session);
            Msg msg = Msg.builder(Msg.MsgType.LOGIN_REQUEST, user)
                    .build();
            loginMsgSender.sendMsg(msg);
            startChat(scanner);
        }
    }

    /**
     * 判断是否登录
     * @return
     */
    public boolean isLogin() {
        if (null == session) {
            logger.info("session is null");
            return false;
        }
        return session.isLogin();
    }
}
