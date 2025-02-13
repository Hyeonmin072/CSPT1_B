package com.myong.backend.jwttoken.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myong.backend.domain.dto.UserLoginRequestDto;
import com.myong.backend.jwttoken.dto.TokenDto;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.jwttoken.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;


public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtService jwtService, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;

        // 필터가 처리할 로그인 URL 설정
        setFilterProcessesUrl("/user/signin");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException  {
        try{
            UserLoginRequestDto userLoginRequestDto = objectMapper.readValue(request.getInputStream(),UserLoginRequestDto.class);

            // 사용자 입력값을 기반으로 인증 토큰 생성
            String username = userLoginRequestDto.getEmail();
            String password = userLoginRequestDto.getPassword();
            System.out.println("요청들어온 userName:"+username);
            System.out.println("요청들어온 password:"+password);

            if (username == null || password == null) {
                throw new IllegalArgumentException("Invalid parameter: username or password is missing");
            }

            // Username과 Password 기반으로 인증 토큰 생성 및 인증 시도
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password,AuthorityUtils.createAuthorityList("ROLE_USER"));
            System.out.println();
            System.out.println("인증이 성공하였습니다");
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e){
            throw new RuntimeException("Failed to parse login request", e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        // 인증 성공 후 액세스 토큰과 리프레시 토큰 생성
        UserDetailsDto userDetailsDto = (UserDetailsDto) authentication.getPrincipal();
        System.out.println(userDetailsDto.getUsername());
        System.out.println(userDetailsDto.getPassword());
        System.out.println(authentication.getName());
        System.out.println(authentication.getAuthorities());
        String username = userDetailsDto.getUsername();
        String role = userDetailsDto.getAuthorities().iterator().next().getAuthority();
        System.out.println("role :"+role);


        // JWT 토큰 생성
        String accessToken = jwtService.createAccessToken(username, role);

        // RefreshToken 엔티티를 Redis에 저장 (TTL 기반 만료)
        jwtService.saveRedisRefreshToken(username);

        // 응답으로 토큰 반환
        TokenDto tokenDto = new TokenDto(accessToken);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), tokenDto);
    }

}
