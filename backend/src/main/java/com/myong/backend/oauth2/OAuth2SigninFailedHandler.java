package com.myong.backend.oauth2;


import com.myong.backend.oauth2.exception.CustomOAuth2AuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2SigninFailedHandler implements AuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2SigninFailedHandler.class);
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if(exception instanceof CustomOAuth2AuthenticationException customOAuth2AuthenticationException){
            System.out.println("소셜 최초 로그인");
            String email = customOAuth2AuthenticationException.getEmail();
            String name = customOAuth2AuthenticationException.getName();

            // URL 인코딩
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString());
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());

            System.out.println("리다이렉션중 url: "+"http://localhost:5173/social/signup?email="+encodedEmail+"&name="+encodedName);
            // 성공시 회원가입 폼 리다이렉트
            response.sendRedirect("http://localhost:5173/social/signup?email="+encodedEmail+"&name="+encodedName);
        }else{

            String errorMessage = exception.getMessage();
            String code = request.getParameter("code"); // 인가 코드 확인
            logger.error("OAuth2 로그인 실패! 이유: {} | code: {}", errorMessage, code);
            // 실패시 홈페이지에 상태 전달
            response.sendRedirect("http://localhost:5173?signin-status=fail");
        }
    }
}
