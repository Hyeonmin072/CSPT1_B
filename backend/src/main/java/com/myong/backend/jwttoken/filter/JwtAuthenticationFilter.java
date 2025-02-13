//package com.myong.backend.jwttoken.filter;
//
//import com.myong.backend.jwttoken.JwtTokenProvider;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.GenericFilterBean;
//
//import java.io.IOException;
//
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends GenericFilterBean {
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//
//        String authorizationHeader = httpRequest.getHeader("Authorization");
//        System.out.println(authorizationHeader);
//
//        if (authorizationHeader == null) {
//            chain.doFilter(request, response);
//            return ;
//        }
//
//        String accessToken = resolveToken((HttpServletRequest) request);
//        // 1. Request Header 에서 JWT 토큰 추출
//
//        System.out.println("doFilter실행");
//
//        // 2. validateToken 으로 토큰 유효성 검사
//        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
//            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
//            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        chain.doFilter(request, response);
//    }
//
//    // Request Header 에서 토큰 정보 추출
//    private String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        System.out.println("헤더부분:"+bearerToken);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
//            System.out.println("헤더 Bearer제거 :"+bearerToken.substring(7).trim());
//            return bearerToken.substring(7).trim();
//        }
//        return null;
//    }
//
//
//}