package com.bigyj.server.session;

import com.bigyj.entity.MsgDto;
import com.bigyj.server.utils.SpringContextUtil;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisRemoteSession implements ServerSession{
	@Override
	public boolean writeAndFlush(MsgDto pkg) {
		RedisTemplate redisTemplate = SpringContextUtil.getBean(RedisTemplate.class);
		redisTemplate.convertAndSend("messagepush", "Hello message !");
		return true;
	}

	@Override
	public String getSessionId() {
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public String getUserId() {
		return null;
	}
}
