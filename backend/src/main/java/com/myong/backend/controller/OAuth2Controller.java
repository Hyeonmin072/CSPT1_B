package com.myong.backend.controller;

import com.myong.backend.domain.dto.oauth2.SocialSignupRequestDto;
import com.myong.backend.domain.entity.user.SigninType;
import com.myong.backend.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;


    @PostMapping("/kakao/signup")
    public ResponseEntity<?> kakaoSignup(@RequestBody SocialSignupRequestDto requestDto){
        return oAuth2Service.SocialSignup(requestDto,SigninType.KAKAO);
    }

    @GetMapping("google/signup")
    public ResponseEntity<?> googleSignup(@RequestBody SocialSignupRequestDto requestDto){
        return oAuth2Service.SocialSignup(requestDto,SigninType.GOOGLE);
    }




}
