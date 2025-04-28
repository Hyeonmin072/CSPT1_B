//package com.myong.backend.service.Alarm;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class RedisSubscriber {
//
//    private final SseEmitterManager emitterManager;
//
//    public RedisSubscriber(SseEmitterManager emitterManager) {
//        this.emitterManager = emitterManager;
//    }
//
//    public void onMessage(Message message, byte[] pattern) {
//        String channel = new String(message.getChannel());// 채널이름 추출
//        String[] parts = channel.split(":"); // 채널에서 유형(type)과 ID 분리
//        String type = parts[0];
//        String id = parts[1];
//        String content = new String(message.getBody());
//
//        emitterManager.send(type, id, content); // 메시지 전송
//    }
//}
