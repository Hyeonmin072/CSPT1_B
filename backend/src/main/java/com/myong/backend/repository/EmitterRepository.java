package com.myong.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(String userId){
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, sseEmitter);
        return sseEmitter;
    }

    public SseEmitter findByUserId(String userId){
        if(!emitters.containsKey(userId)){
            return null;
        }
        return emitters.get(userId);
    }

    public void deleteByUserId(String userId){
        emitters.remove(userId);
    }
}
