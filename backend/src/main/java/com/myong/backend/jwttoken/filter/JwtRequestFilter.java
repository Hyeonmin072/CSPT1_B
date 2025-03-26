package com.myong.backend.jwttoken.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtRequestFilter(JwtService jwtService,ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        System.out.println(request.getRequestURI());

        if (request.getRequestURI().equals("/signin")) {
            System.out.println("로그인으로 요청");
            filterChain.doFilter(request, response); // 로그인 요청일 경우 토큰 검사 없이 바로 진행
            return;
        }

        // 쿠키에서 "accessToken" 값을 추출
        String token = jwtService.getTokenFromCookie(request);

        // 쿠키에 토큰이 없거나 비어있는 경우 다음 필터로 전달
        if (StringUtils.isBlank(token)) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            // 토큰이 만료되었거나 유효하지 않으면 새 토큰 발급
            if (jwtService.isExpired(token)) {
                if (!jwtService.isValidToken(token)) {
                    throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
                }

                String userName = jwtService.getUserName(token);
                String name = jwtService.getName(token);
                String role = jwtService.getUserRole(token);

                if(jwtService.refreshTokenIsExpired(userName)){
                    String newAccessToken = jwtService.createAccessToken(userName,name,role);
                    jwtService.deleteRedisRefreshToken(userName);
                    jwtService.saveRedisRefreshToken(userName);

                    // 스프링 시큐리티 인증 토큰 생성 및 SecurityContext에 설정
                    UserDetailsDto userDetailsDto = new UserDetailsDto(userName,role,name);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsDto, null, userDetailsDto.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                            .httpOnly(true)
                            .secure(false)                       // 테스트환경에선 false
                            .path("/")
                            .maxAge(60 * 60)        // 1시간 유효
                            .sameSite("Lax")                     // CSRF 방지
                            .build();

                    response.addHeader("Set-Cookie", accessTokenCookie.toString());

                    return ;
                }
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰에서 사용자 정보 추출
            String userName = jwtService.getUserName(token);
            String role = jwtService.getUserRole(token);
            String name = jwtService.getName(token);

            System.out.println("userName : "+userName);
            System.out.println("role : "+role);
            UserDetailsDto userDetailsDto = new UserDetailsDto(userName,role,name);

            System.out.println("사용자 정보 추출:"+userDetailsDto.getAuthorities());
            System.out.println("사용자 정보 추출:"+userDetailsDto.getUsername());

            // 스프링 시큐리티 인증 토큰 생성 및 SecurityContext에 설정
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsDto, null, userDetailsDto.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);


            filterChain.doFilter(request, response);


        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었을 경우에도 다음 필터로 전달
            filterChain.doFilter(request, response);

        }

    }

}
