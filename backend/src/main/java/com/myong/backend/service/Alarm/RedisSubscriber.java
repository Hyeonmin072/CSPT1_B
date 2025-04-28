//package com.myong.backend.service.Alarm;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class RedisSubscriber {
//
//    private final SseEmitterManager emitterManager;
//
//    public RedisSubscriber(SseEmitterManager emitterManager) {
//        this.emitterManager = emitterManager;
//    }
//
//    // 반드시 public으로 선언하고, 올바른 파라미터 형식을 가져야 함
//    public void onMessage(Message message, byte[] pattern) {
//        String channel = new String(message.getChannel()); // 채널 이름 추출
//        String[] parts = channel.split(":"); // 채널에서 type과 id 분리
//        String type = parts[0]; // user, designer, shop 등 유형
//        String id = parts[1]; // ID 값
//        String content = new String(message.getBody()); // 메시지 내용
//
//        // 로그 출력 (디버깅용)
//        log.info("Received message - type: {}, id: {}, content: {}", type, id, content);
//
//        // 메시지 처리 후 SSE 연결로 전달
//        emitterManager.send(type, id, content);
//    }
//}