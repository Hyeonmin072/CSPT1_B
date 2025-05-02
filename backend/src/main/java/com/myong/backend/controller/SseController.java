//package com.myong.backend.controller;
//
//import com.myong.backend.service.Alarm.SseEmitterManager;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//@Slf4j
//@RestController
//@RequestMapping("/sse")
//public class SseController {
//    private final SseEmitterManager emitterManager;
//
//    public SseController(SseEmitterManager emitterManager) {
//        this.emitterManager = emitterManager;
//    }
//
//    @GetMapping("/connect")
//    public SseEmitter connect() {
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        String type = authentication.getAuthorities().stream()
//                .findFirst()
//                .map(Object::toString)
//                .orElse("default");
//
//        log.info("type: {}", type);//디버깅용
//
//        String id = authentication.getName();//토큰에서 id를 추출
//
//        log.info("id: {}", id);//디버깅용
//
//        return emitterManager.connect(type, id);
//    }
//
//}
