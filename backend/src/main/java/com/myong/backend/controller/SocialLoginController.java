package com.myong.backend.controller;

import com.myong.backend.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class SocialLoginController {

    private final OAuth2Service oAuth2Service;

    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoSignin(@RequestParam("code") String code) throws IOException {
        return oAuth2Service.kakaoSignin(code);
    }
}
