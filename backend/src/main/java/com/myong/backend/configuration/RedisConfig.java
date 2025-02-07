package com.myong.backend.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableRedisRepositories
@RequiredArgsConstructor
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Value("${spring.mail.redis.host}")
    private String host;
    @Value("${spring.mail.redis.port}")
    private int port;

    //IoC container를 통해 lettuce connector
    //PersistenceExceptionTranslator 역할을 수행
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, String> redisTpl() {
        RedisTemplate<String, String> redisTpl = new RedisTemplate<>();
        redisTpl.setKeySerializer(new StringRedisSerializer());
        redisTpl.setValueSerializer(new StringRedisSerializer());
        redisTpl.setConnectionFactory(redisConnectionFactory());

        return redisTpl;
    }
}
