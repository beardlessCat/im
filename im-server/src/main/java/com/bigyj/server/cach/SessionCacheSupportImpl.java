package com.bigyj.server.cach;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.bigyj.entity.SessionCache;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
@Component
public class SessionCacheSupportImpl implements SessionCacheSupport{
	public static final String REDIS_PREFIX = "SessionCache:id:";
	@Autowired
	protected RedisTemplate redisTemplate;
	//2小时需要重新登录（秒）
	private static final long VALIDITY_TIME = 60 * 60 * 2;

	@Override
	public void save(final SessionCache sessionCache)
	{
		String key = REDIS_PREFIX + sessionCache.getSessionId();
		String value = new Gson().toJson(sessionCache);
		redisTemplate.opsForValue().set(key, value, VALIDITY_TIME, TimeUnit.SECONDS);
	}


	@Override
	public SessionCache get(final String sessionId)
	{
		String key = REDIS_PREFIX + sessionId;
		String value = (String) redisTemplate.opsForValue().get(key);

		if (!StringUtils.isEmpty(value))
		{
			return JSONObject.parseObject(value, SessionCache.class);
		}
		return null;
	}

	@Override
	public void remove( String sessionId)
	{
		String key = REDIS_PREFIX + sessionId;
		redisTemplate.delete(key);
	}
}
