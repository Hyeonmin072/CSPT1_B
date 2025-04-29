package com.myong.backend.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myong.backend.domain.dto.Alarm.MessageDto;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisSubscriber implements MessageListener {
    private final ObjectMapper om;
    private final EmitterRepository emitterRepository;
    private final JwtService jwtService;
    @Override
    public void onMessage(Message message, byte[] pattern) {

        log.info("RedisSubscriber onMessage");

        MessageDto messageDto = serialize(message);
        String token = messageDto.getJwtToken();
        String userId = jwtService.getUserName(token);
        String role = jwtService.getUserRole(token);

        SseEmitter emitter = emitterRepository.findByUserId(userId);
        if (emitter == null) {
            emitter = emitterRepository.save(userId);
        }

    }

    private MessageDto serialize(final Message message) {
        try {
            return this.om.readValue(message.getBody(), MessageDto.class);
        }catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void SendToClient(){

    }
}
