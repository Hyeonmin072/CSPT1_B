package com.myong.backend.controller;


import com.myong.backend.domain.dto.TokenInfo;
import com.myong.backend.domain.dto.UserLoginRequestDto;
import com.myong.backend.jwttoken.JwtTokenProvider;
import com.myong.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

//    @PostMapping("/signin")
//    public TokenInfo login(@RequestBody UserLoginRequestDto userLoginRequestDto){
//        String email = userLoginRequestDto.getEmail();
//        String password = userLoginRequestDto.getPassword();
//        TokenInfo tokenInfo = userService.login(email,password);
//        return tokenInfo;
//    }

    @PostMapping("/signin")
    public ResponseEntity<TokenInfo> login(
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken,
            @RequestBody(required = false) UserLoginRequestDto userLoginRequestDto) {


        System.out.println(" 요청받은 Access Token: " + accessToken);
        System.out.println(" 요청받은 Refresh Token: " + refreshToken);
        if (accessToken != null && accessToken.startsWith("Bearer")) {
            accessToken = accessToken.substring(7).trim(); // "Bearer " 부분을 제거
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        // 현재 Access Token이 유효한 경우, 새로 로그인할 필요 없음
        if (authentication != null) //&& accessToken != null && refreshToken != null) {
        {
            System.out.println("사용자 이메일:"+authentication.getName());
            System.out.println("어세스 토큰이 유효");
            return ResponseEntity.ok(new TokenInfo("Bearer", accessToken, refreshToken));
        }


        // Access Token이 유요하지 않아, RefreshToken이 유효한지 확인
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken) ) {

            System.out.println("리프레시 토큰 유효");
            // Refresh Token이 유효하면 새로운 Access Token & Refresh Token 발급
            Authentication refreshAuth = jwtTokenProvider.getAuthentication(accessToken);
            TokenInfo newTokenInfo = jwtTokenProvider.generateToken(refreshAuth);
            return ResponseEntity.ok(newTokenInfo);
        }

        // AccessToken 과 Refresh Token 이 만료되어서 401 에러를 던짐
        if (userLoginRequestDto == null) {
            System.out.println("어세스 와 리프레시 둘 다 유효하지않음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenInfo("Bearer",  null, null));
        }

        //  기존 인증 정보가 없거나, 토큰이 만료되었으면 새로운 로그인 처리
        TokenInfo tokenInfo = userService.login(userLoginRequestDto.getEmail(), userLoginRequestDto.getPassword());
        System.out.println(" 발급된 Access Token: " + tokenInfo.getAccessToken());
        System.out.println(" 발급된 Refresh Token: " + tokenInfo.getRefreshToken());
        return ResponseEntity.ok(tokenInfo);
    }

}