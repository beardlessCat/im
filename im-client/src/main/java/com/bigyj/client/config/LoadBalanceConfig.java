package com.bigyj.client.config;

import com.bigyj.client.load.balance.LoadBalance;
import com.bigyj.client.load.balance.RandomLoadBalance;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadBalanceConfig {
	@Bean
	public LoadBalance loadBalance(){
		return new RandomLoadBalance();
	}
}
