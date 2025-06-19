package com.example.osid.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager("models", "options");
		cacheManager.setCaffeine(Caffeine.newBuilder()
			.maximumSize(100)
			.expireAfterWrite(1, TimeUnit.MINUTES) // ttl 테스트용 1분
		);
		cacheManager.setAllowNullValues(false);
		return cacheManager;
	}
}
