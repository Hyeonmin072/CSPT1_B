package com.myong.backend.controller;

import okhttp3.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/sociallogin")
public class SocialLoginController {

    private static final String CLIENT_ID = "YOUR_REST_API_KEY";
    private static final String REDIRECT_URI = "http://localhost:1271/api/sociallogin/kakao";

    @GetMapping("/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestParam("code") String code) throws IOException {
        // 액세스 토큰 요청 URL
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", CLIENT_ID)
                .add("redirect_uri", REDIRECT_URI)
                .add("code", code)
                .build();

        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body() != null ? response.body().string() : "";

        // 액세스 토큰과 함께 필요한 정보를 응답
        return ResponseEntity.ok(responseBody);
    }
}
