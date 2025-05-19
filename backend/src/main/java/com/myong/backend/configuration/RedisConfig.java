package com.myong.backend.configuration;

//import com.myong.backend.service.Alarm.RedisSubscriber;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myong.backend.domain.dto.Alarm.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
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
    public RedisTemplate <String, MessageDto> messageRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper)//objectMapper는 직렬화와 역직렬화용으로 사용
    {
        RedisTemplate<String, MessageDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        //ObjectMapper 설정
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);//작성시간 Redis에 등록

        // GenericJackson2JsonRedisSerializer 사용
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // 직렬화기 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setKeySerializer(jsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(jsonRedisSerializer);

        return redisTemplate;
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