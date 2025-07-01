// package com.example.osid.config;
//
// import java.time.Duration;
// import java.util.HashMap;
// import java.util.Map;
//
// import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.cache.RedisCacheConfiguration;
// import org.springframework.data.redis.cache.RedisCacheManager;
// import org.springframework.data.redis.connection.RedisConnectionFactory;
// import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
// import org.springframework.data.redis.serializer.RedisSerializationContext;
// import org.springframework.data.redis.serializer.StringRedisSerializer;
//
// @Configuration
// @EnableCaching
// public class RedisCacheConfig {
//
// 	@Bean
// 	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
// 		RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
// 			.entryTtl(Duration.ofMinutes(5))
// 			.disableCachingNullValues()
// 			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
// 			.serializeValuesWith(
// 				RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
//
// 		Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
// 		cacheConfigs.put("models", defaultConfig.entryTtl(Duration.ofMinutes(1))); // 테스트용 ttl 1분 설정
// 		cacheConfigs.put("options", defaultConfig.entryTtl(Duration.ofMinutes(1)));
//
// 		return RedisCacheManager.builder(connectionFactory)
// 			.cacheDefaults(defaultConfig)
// 			.withInitialCacheConfigurations(cacheConfigs)
// 			.build();
// 	}
// }
