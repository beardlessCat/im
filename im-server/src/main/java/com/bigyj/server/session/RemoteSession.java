package com.bigyj.server.session;

import com.bigyj.entity.MsgDto;
import com.bigyj.server.utils.SpringContextUtil;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;

@Data
public class RemoteSession implements ServerSession {
	@Override
	public boolean writeAndFlush(MsgDto msg) {
		RedisTemplate redisTemplate = SpringContextUtil.getBean(RedisTemplate.class);
		redisTemplate.convertAndSend("imMessage", new Gson().toJson(msg));
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
