package com.myong.backend.service;

import com.myong.backend.domain.dto.TokenInfo;
import com.myong.backend.jwttoken.JwtTokenProvider;
import com.myong.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenInfo login(String email,String password){

        // email/pw  기반으로 Authentication 객체생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,password);

        //실제 검증(사용자 비밀번호 체크)가 이루어지는 부분
        // authenticate 실행될 때 CustomUserDetailsService에서 LoadUserByUsername 매서드가 자동 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증정보 기반 JWT 토큰생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        return tokenInfo;

    }
}
