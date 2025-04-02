package com.myong.backend.controller;

import com.myong.backend.domain.dto.oauth2.KakaoSignupRequestDto;
import com.myong.backend.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) throws IOException {
        System.out.println("코드받음"+code);
        return oAuth2Service.kakaoCallback(code);
    }

    @PostMapping("/kakao/signin")
    public ResponseEntity<?> kakaoSignin(@RequestBody Map<String,String> request) throws IOException {
        return oAuth2Service.kakaoSignin(request.get("code"));
    }

    @GetMapping("/kakao/signup")
    public ResponseEntity<?> kakaoSignup(@RequestBody KakaoSignupRequestDto requestDto){
        return oAuth2Service.kakaoSignup(requestDto);
    }

}
