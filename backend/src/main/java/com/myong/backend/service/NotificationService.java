package com.myong.backend.service;

import com.myong.backend.domain.dto.NotificationSendResponseDto;
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
        // 매 연결마다 고유한 이벤트 아이디 생성
        String eventId = userEmail + "_" + System.currentTimeMillis();
        
        // SseEmitter 인스턴스 생성 후 Map에 저장
        SseEmitter emitter = emitterRepository.save(eventId, new SseEmitter(DEFAULT_TIMEOUT));

        // 이벤트 전송 시, 자동으로 삭제
        emitter.onCompletion(() -> {
            log.info("onCompletion 콜백");
            emitterRepository.deleteById(eventId);
        });
        
        // 이벤트 전송 시, 시간 초과가 되면 자동으로 삭제
        emitter.onTimeout(() -> {
            log.info("onTimeout 콜백");
            emitterRepository.deleteById(eventId);
                    
        });

        // 최초 연결 시 응답을 보내지 않으면 503 오류가 발생 -> 오류 방지를 위해 최초 연결시 더미 이벤트 생성 후 클라이언트에 전송
        sendToClient(eventId, emitter, "알림 서버 연결 성공. [userEmail=" + userEmail + "]");

        // 클라이언트가 미수신한 이벤트 목록이 존재할 경우 -> 모두 전송하여 이벤트 유실 예방
        // Last-Event-Id가 있으면 여기서 사용된다 -> 전송되지 않고 쌓인 이벤트들을 찾아 보내기 위해 사용
        if(!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByuserEmail(userEmail);
            events.entrySet().stream()
                    .filter(e -> lastEventId.compareTo(e.getKey()) < 0)
                    .forEach(e -> sendToClient(e.getKey(), emitter, e.getValue()));
        }

        return emitter;
    }

    /**
     * 다른 서비스 클래스에서 이벤트 발생 시 이 메서드를 호출하여 알림을 보낸다
     * @param notification 알람 객체(이 객체를 DB에 저장한 후 넘어올 것으로 기대)
     */
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
                        sendToClient(eventId, (SseEmitter) emitter, new NotificationSendResponseDto(eventId, notification.getContent()));
                    } catch (Exception e) {
                        emitterRepository.deleteById(key); // 실패한 emitter 제거
                        log.error("알람 전송 실패", e);
                    }
                }
        );

    }

    /**
     * 서버 측 이벤트 -> 클라이언트로 직접 보내는 메서드
     * @param eventId 이벤트 아이디
     * @param emitter SseEmitter 객체
     * @param object 데이터
     */
    private void sendToClient(String eventId, SseEmitter emitter, Object object) {
        try {
            emitter.send(SseEmitter.event().name("connect").id(eventId).data(object));
        } catch (IOException e) {
            emitterRepository.deleteById(eventId);
            throw new RuntimeException("알림 서버 연결 오류");
        }
    }
}
