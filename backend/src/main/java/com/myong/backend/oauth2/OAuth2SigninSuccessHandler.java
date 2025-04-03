package com.myong.backend.oauth2;


import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SigninSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("소셜 로그인 성공 핸들러");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        if(oAuth2User instanceof UserDetailsDto userDetailsDto){
            String email = userDetailsDto.getUsername();
            String name = userDetailsDto.getName();
            String role = userDetailsDto.getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("권한 없음"))
                    .getAuthority();

            ResponseCookie accessTokenCookie = getAccessTokenCookie(email,name,role);
            response.addHeader("Set-Cookie",accessTokenCookie.toString());

            response.sendRedirect("http://localhost:5173");
        }

    }

    // 쿠키 생성
    public ResponseCookie getAccessTokenCookie (String email, String name, String role){
        // 어세스 토큰생성
        String userAccessToken = jwtService.createAccessToken(email,name,role);
        // 리프레시 토큰저장
        jwtService.saveRedisRefreshToken(email);

        return ResponseCookie.from("accessToken", userAccessToken)
                .httpOnly(true)
                .secure(false)
                .maxAge(60*60)
                .path("/")
                .sameSite("Lax")
                .build();

    }
}
