package com.myong.backend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.jwttoken.filter.JwtLoginFilter;
import com.myong.backend.jwttoken.filter.JwtRequestFilter;
import com.myong.backend.oauth2.OAuth2SigninFailedHandler;
import com.myong.backend.oauth2.OAuth2SigninSuccessHandler;
import com.myong.backend.oauth2.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final OAuth2SigninFailedHandler oAuth2SigninFailedHandler;
    private final OAuth2SigninSuccessHandler oAuth2SigninSuccessHandler;
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager, PrincipalOauth2UserService principalOauth2UserService) throws Exception {
        System.out.println("Setting up security filter chain");
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/signin","/designer/signin","/shop/signin","/user/signup","/designer/signup","shop/signup"
                        ,"/email/**","user/email/check/","/designer/email/check/","/shop/checkemail/**","/user/checkemail/**","/designer/checkemail/**","/toss", "/payment/**").permitAll()
//                        .requestMatchers("/user/**").hasRole("USER")
//                        .requestMatchers("/designer/**").hasRole("DESIGNER")   // 배포환경에선 권한마다 시큐리티부여
//                        .requestMatchers("/shop/**").hasRole("SHOP")
                        .anyRequest().permitAll()   // 테스트환경에선 모든 요청 펄밋
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/social-signin") // OAuth2 인증 요청 URL
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/api/oauth2/callback/*")
                        )
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(principalOauth2UserService))  // OAuth2 서비스 진행
                        .successHandler(oAuth2SigninSuccessHandler)  // 성공 핸들러
                        .failureHandler(oAuth2SigninFailedHandler)   // 실패 핸들러
                )
                .httpBasic(withDefaults())
                .anonymous(anonymous -> anonymous.disable())
                .addFilterAt(new JwtRequestFilter(jwtService, objectMapper), JwtLoginFilter.class)
                .addFilterBefore(new JwtLoginFilter(authenticationManager, jwtService, objectMapper), UsernamePasswordAuthenticationFilter.class);



        System.out.println("Security filter chain setup complete");
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
