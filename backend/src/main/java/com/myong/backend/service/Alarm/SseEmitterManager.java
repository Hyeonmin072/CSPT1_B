//package com.myong.backend.service.Alarm;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class SseEmitterManager {
//
//    private final Map<String, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();
//
//    public SseEmitter connect(String type, String id) {
//        SseEmitter emitter = new SseEmitter(60 * 1000L); // 1분 타임아웃
//        emitters.putIfAbsent(type, new ConcurrentHashMap<>());
//        emitters.get(type).put(id, emitter);
//
//        emitter.onCompletion(() -> emitters.get(type).remove(id));
//        emitter.onTimeout(() -> emitters.get(type).remove(id));
//        return emitter;
//    }
//
//    public void send(String type, String id, String message) {
//        Map<String, SseEmitter> typeEmitters = emitters.get(type);
//        if (typeEmitters != null) {
//            SseEmitter emitter = typeEmitters.get(id);
//            if (emitter != null) {
//                try {
//                    emitter.send(SseEmitter.event().name("notification").data(message));
//                } catch (IOException e) {
//                    typeEmitters.remove(id); // 오류 시 제거
//                }
//            }
//        }
//    }
//}
//
