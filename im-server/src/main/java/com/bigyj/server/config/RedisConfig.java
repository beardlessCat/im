package com.bigyj.server.config;

import com.bigyj.server.msg.MessageReceive;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
@Configuration
public class RedisConfig {
	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		//配置redisTemplate
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		// 配置连接工厂
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		RedisSerializer stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);//key序列化
		redisTemplate.setHashKeySerializer(stringSerializer);//Hash key序列化
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("imMessage"));
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(MessageReceive receiver) {
		//这个地方 是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“receiveMessage”
		//也有好几个重载方法，这边默认调用处理器的方法 叫handleMessage 可以自己到源码里面看
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
}
