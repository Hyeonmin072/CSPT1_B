package com.myong.backend.configuration;

//import com.myong.backend.service.Alarm.RedisSubscriber;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableRedisRepositories
@RequiredArgsConstructor
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
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
    @Qualifier("redisTpl")
    public RedisTemplate<String, String> redisTpl() {
        RedisTemplate<String, String> redisTpl = new RedisTemplate<>();
        redisTpl.setKeySerializer(new StringRedisSerializer());
        redisTpl.setValueSerializer(new StringRedisSerializer());
        redisTpl.setConnectionFactory(redisConnectionFactory());

        return redisTpl;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
//    // Redis Pub/Sub 리스너 컨테이너 설정
//    @Bean
//    public RedisMessageListenerContainer listenerContainer(
//            RedisConnectionFactory connectionFactory,
//            MessageListenerAdapter listenerAdapter) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.addMessageListener(listenerAdapter, new PatternTopic("*:*")); // 모든 채널 구독
//        return container;
//    }
//
//    // Pub/Sub 메시지 리스너 어댑터 설정
//    @Bean
//    public MessageListenerAdapter listenerAdapter(RedisSubscriber redisSubscriber) {
//        return new MessageListenerAdapter(redisSubscriber, "onMessage"); // "onMessage" 메서드 연결
//    }
//
}