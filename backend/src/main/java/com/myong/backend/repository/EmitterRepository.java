package com.myong.backend.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    public SseEmitter save(String emitterId, SseEmitter sseEmitter);
    public void saveEventCache(String emitterId, Object event);
    public Map<String, SseEmitter> findAllEmitterStartWithByuserEmail(String userEmail);
    public Map<String, Object> findAllEventCacheStartWithByuserEmail(String userEmail);
    public void deleteById(String emitterId);
    public void deleteAllEmitterStartWithEmail(String userEmail);
    public void deleteAllEventCacheStartWithEmail(String userEmail);
}
