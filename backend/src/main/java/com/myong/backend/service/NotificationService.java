package com.myong.backend.service;

import com.myong.backend.domain.entity.Notification;
import com.myong.backend.repository.EmitterRepository;
import com.myong.backend.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final EmitterRepository  emitterRepository;
    private final NotificationRepository notificationRepository;

    // 연결 지속 시간 -> 1시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter connect(String userEmail, String lastEventId) {
        // 매 연결마다 고유한 아이디 생성
        String emitterId = userEmail + "_" + System.currentTimeMillis();
        
        // SseEmitter 인스턴스 생성 후 Map에 저장
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // 이벤트 전송 시, 비동기 요청이 안되거나 시간 초과가 되면 자동으로 삭제
        emitter.onCompletion(() -> {
            log.info("onCompletion 콜백");
            emitterRepository.deleteById(emitterId);
        });
        
        emitter.onTimeout(() -> {
            log.info("onTimeout 콜백");
            emitterRepository.deleteById(emitterId);
                    
        });

        // 최초 연결 시 더미 Event가 없으면 503 오류가 발생 -> 오류 방지를 위해 더미 Event 생성 후 클라이언트에 전송
        sendToClient(emitterId, emitter, "알림 서버 연결 성공. [userEmail=" + userEmail + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 -> 모두 전송
        if(!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByuserEmail(userEmail);
            events.entrySet().stream()
                    .filter(e -> lastEventId.compareTo(e.getKey()) < 0)
                    .forEach(e -> sendToClient(e.getKey(), emitter, e.getValue()));
        }

        return emitter;
    }

    public void send(Notification notification) {
        String eventId = notification.getUser().getEmail() + "_" + System.currentTimeMillis();

        // 유저의 SseEmitter 모둑 가져오기
        Map<String, Object> sseEmiters = emitterRepository.findAllEventCacheStartWithByuserEmail(notification.getUser().getEmail());
        sseEmiters.forEach(
                (key, emitter) -> {
                    try {
                        // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                        emitterRepository.saveEventCache(key, notification);
                        // 데이터 전송
                        sendToClient(eventId, (SseEmitter) emitter, "머지");
                    } catch (Exception e) {
                        emitterRepository.deleteById(key); // 실패한 emitter 제거
                        log.error("알람 전송 실패", e);
                    }
                }
        );

    }

    private void sendToClient(String eventId, SseEmitter emitter, Object object) {
        try {
            emitter.send(SseEmitter.event().name("connect").id(eventId).data(object));
        } catch (IOException e) {
            emitterRepository.deleteById(eventId);
            throw new RuntimeException("알림 서버 연결 오류");
        }
    }
}
