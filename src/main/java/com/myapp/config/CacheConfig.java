package com.myapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * I want to use {@link org.springframework.cloud.aws.cache.config.annotation.EnableElastiCache}
 * which supports cluster for prod environment. But it has a problem shown on https://github.com/spring-cloud/spring-cloud-aws/issues/93 .
 * As a result, I use a single redis server through RedisCacheManager.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Autowired
    public CacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisCacheManager(redisTemplate);
    }
}
