package com.myong.backend.controller;

import com.myong.backend.domain.dto.oauth2.KakaoSignupRequestDto;
import com.myong.backend.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoSignin(@RequestParam("code") String code) throws IOException {
        return oAuth2Service.kakaoSignin(code);
    }

    @GetMapping("/kakao/signup")
    public ResponseEntity<?> kakaoSignup(@RequestBody KakaoSignupRequestDto requestDto){
        return oAuth2Service.kakaoSignup(requestDto);
    }
}
