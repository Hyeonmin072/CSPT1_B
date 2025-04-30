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
import org.elasticsearch.client.ResponseException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtRequestFilter(JwtService jwtService,ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        System.out.println("요청 들어온 uri : "+uri);
        List<String> mustBeAuthenticatedEndpoints = Arrays.asList("/user/loadheader","/user/profile","/user/like-designerpage"
                ,"/user/designerlike","/user/reservation","/user/location","/user/allcoupons","/user/review","/user/payment"
                ,"/designer","/shop");

        List<String> allowEndpoints = Arrays.asList("/designer/signup","/designer/checkemail/","/designer/nickname",
                                                    "/email","/api/oauth2",
                                                    "/shop/signup","/shop/bizid","/shop/certification","/shop/checkemail");

        if (uri.equals("/signin")) {
            System.out.println("로그인으로 요청");
            filterChain.doFilter(request, response); // 로그인 요청일 경우 토큰 검사 없이 바로 진행
            return;
        }

        for(String endPoint : allowEndpoints){
            if(uri.startsWith(endPoint)){
                System.out.println("검증이 필요없는 엔드포인트");
                filterChain.doFilter(request,response);
                return;
            }
        }
        // 쿠키에서 "accessToken" 값을 추출
        String token = jwtService.getTokenFromCookie(request);

        System.out.println(token);

        if(!StringUtils.isBlank(token)){
            try {
                // 서명 검증
                if (!jwtService.isValidToken(token)) {
                    System.out.println("서명검증 실패");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("유효하지 않은 토큰입니다.");
                    return;
                }
                System.out.println("서명검증 통과");

                // 토큰 만료 검증
                if (jwtService.isExpired(token)) {
                    System.out.println("토큰이 만료되었음.");
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
                        filterChain.doFilter(request, response);
                    }
                    filterChain.doFilter(request, response);
                    return;
                }

                System.out.println("토큰이 만료되지 않았음");
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
                return;

            } catch (ExpiredJwtException e) {
                // 토큰이 만료되었을 경우에도 다음 필터로 전달
                filterChain.doFilter(request, response);
                return;
            }
        }

        for(String entPoint : mustBeAuthenticatedEndpoints){
            if(uri.startsWith(entPoint)){
                System.out.println("검증이 필요한 엔드포인트");
               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               response.getWriter().write("로그인이 필요한 서비스입니다.");
               return ;
            }
        }

        filterChain.doFilter(request, response);
        return;
    }

}
