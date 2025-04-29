package com.myong.backend.service.Alarm;

import com.myong.backend.domain.dto.Alarm.MessageDto;
import com.myong.backend.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessageService {
    private static final String CHANNEL_PREFIX = "channel:";
    private final RedisMessageListenerContainer container;
    private final EmitterRepository emitterRepository;
    private final RedisTemplate<String, MessageDto> redisTemplate;

}
