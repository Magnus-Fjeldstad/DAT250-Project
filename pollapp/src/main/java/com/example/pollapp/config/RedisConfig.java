package com.example.pollapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisConfig {

    @Bean
    public JedisPooled jedisPooled(
            @Value("${app.redis.url:redis://localhost:6379}") String redisUrl
    ) {
        return new JedisPooled(redisUrl);
    }
}
