package com.myong.backend.jwttoken.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myong.backend.domain.dto.UserLoginRequestDto;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.jwttoken.dto.TokenDto;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtRequestFilter(JwtService jwtService,ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println(request.getRequestURI());
        UserLoginRequestDto userLoginRequestDto = objectMapper.readValue(request.getInputStream(), UserLoginRequestDto.class);

        // 객체의 필드를 로그로 찍기
        System.out.println("Email: " + userLoginRequestDto.getEmail());
        System.out.println("Password: " + userLoginRequestDto.getPassword());
        System.out.println("Who: " + userLoginRequestDto.getWho());
        if (request.getRequestURI().equals("/user/signin") || request.getRequestURI().equals("/designer/signin") || request.getRequestURI().equals("/shop/signin")) {
            System.out.println("여기들어옮");
            filterChain.doFilter(request, response); // 로그인 요청일 경우 토큰 검사 없이 바로 진행
            return;
        }
        // 요청 헤더에서 Authorization 값 추출
        String authorization = request.getHeader(AUTHORIZATION);

        // 헤더가 없거나 Bearer 토큰이 아닌 경우 다음 필터로 전달
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 이후의 실제 토큰 값 추출
        String token = authorization.split(" ")[1];
        System.out.println("token : "+token);

        try {
            // 토큰이 비어있거나 만료된 경우 다음 필터로 전달
            if (StringUtils.isBlank(token) || jwtService.isExpired(token)) {
                if(!jwtService.isValidToken(token)){
                    throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
                }
                String userName = jwtService.getUserName(token);
                String role = jwtService.getUserRole(token);
                if(jwtService.refreshTokenIsExpired(userName)){
                    String newAccessToken = jwtService.createAccessToken(userName,role);
                    jwtService.deleteRedisRefreshToken(userName);
                    jwtService.saveRedisRefreshToken(userName);

                    // 스프링 시큐리티 인증 토큰 생성 및 SecurityContext에 설정
                    UserDetailsDto userDetailsDto = new UserDetailsDto(userName,role);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsDto, null, userDetailsDto.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 응답으로 새 토큰 반환
                    TokenDto tokenDto = new TokenDto(newAccessToken); // newAccessToken 반환
                    response.setContentType("application/json");
                    objectMapper.writeValue(response.getWriter(), tokenDto);

                    return ;
                }
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰에서 사용자 정보 추출
            String userName = jwtService.getUserName(token);
            String role = jwtService.getUserRole(token);

            System.out.println("userName : "+userName);
            System.out.println("role : "+role);
            UserDetailsDto userDetailsDto = new UserDetailsDto(userName,role);

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
