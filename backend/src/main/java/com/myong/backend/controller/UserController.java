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
            @RequestBody UserLoginRequestDto userLoginRequestDto) {
        System.out.println("시발");

        System.out.println("🔹 발급된 Access Token: " + accessToken);
        System.out.println("🔹 발급된 Refresh Token: " + refreshToken);
        //  SecurityContext에 인증 정보가 있는지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("시발2");
        // 현재 Access Token이 유효한 경우, 새로 로그인할 필요 없음
        if (authentication != null && authentication.isAuthenticated() && accessToken != null && refreshToken != null) {
            return ResponseEntity.ok(new TokenInfo("Bearer", accessToken, refreshToken));
        }
        System.out.println("시발3");

        // Access Token이 유요하지 않아, RefreshToken이 유효한지 확인
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken) && accessToken != null && refreshToken != null) {
            // Refresh Token이 유효하면 새로운 Access Token & Refresh Token 발급
            Authentication refreshAuth = jwtTokenProvider.getAuthenticationFromRefreshToken(refreshToken,accessToken);
            TokenInfo newTokenInfo = jwtTokenProvider.generateToken(refreshAuth);
            return ResponseEntity.ok(newTokenInfo);
        }

        System.out.println("시발4");
        // AccessToken 과 Refresh Token 이 만료되어서 401 에러를 던짐
        if (userLoginRequestDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenInfo("aaa",  null, null));
        }

        System.out.println("시발5");
        //  기존 인증 정보가 없거나, 토큰이 만료되었으면 새로운 로그인 처리
        TokenInfo tokenInfo = userService.login(userLoginRequestDto.getEmail(), userLoginRequestDto.getPassword());
        System.out.println("🔹 발급된 Access Token: " + tokenInfo.getAccessToken());
        System.out.println("🔹 발급된 Refresh Token: " + tokenInfo.getRefreshToken());
        return ResponseEntity.ok(tokenInfo);
    }

}