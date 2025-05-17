package com.myong.backend.controller;

import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/user/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // Last-Event-ID는 SSE 연결이 끊어진 경우, 클라이언트가 수신한 마지막 데이터의 ID를 의미한다. 항상 존재하는 것이 아니므로 false
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
                                              @AuthenticationPrincipal UserDetailsDto userDetailsDto) {
        return ResponseEntity.ok(notificationService.connect(userDetailsDto.getUsername(), lastEventId));
    }
}
