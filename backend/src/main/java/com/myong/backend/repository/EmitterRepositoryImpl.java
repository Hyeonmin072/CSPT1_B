package com.myong.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitterMap.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String emitterId, Object event) {
        eventCache.put(emitterId, event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByuserEmail(String userEmail) {
        return emitterMap.entrySet().stream()
                .filter(e -> e.getKey().startsWith(userEmail))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithByuserEmail(String userEmail) {
        return eventCache.entrySet().stream()
                .filter(e -> e.getKey().startsWith(userEmail))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(String emitterId) {
        emitterMap.remove(emitterId);
    }

    @Override
    public void deleteAllEmitterStartWithEmail(String userEmail) {
        emitterMap.forEach(
                (key, emitter) -> {
            if (key.startsWith(userEmail)) {
                emitterMap.remove(key);
            }
        });
    }

    @Override
    public void deleteAllEventCacheStartWithEmail(String userEmail) {
        eventCache.forEach(
                (key, event) -> {
                    if (key.startsWith(userEmail)) {
                        eventCache.remove(key);
                    }
                }
        );
    }
}
