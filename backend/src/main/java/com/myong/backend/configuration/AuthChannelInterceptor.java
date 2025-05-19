package com.myong.backend.configuration;

import com.myong.backend.jwttoken.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AuthChannelInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        // 1. 쿠키에서 accessToken 추출
        List<String> cookieHeaders = request.getHeaders().get(HttpHeaders.COOKIE);
        String accessToken = null;

        if (cookieHeaders != null) {
            for (String cookieHeader : cookieHeaders) {
                List<HttpCookie> cookies = HttpCookie.parse(cookieHeader);
                for (HttpCookie cookie : cookies) {
                    if (cookie.getName().equals("accessToken")) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (accessToken == null) {
            System.out.println(" WebSocket handshake 실패: accessToken 없음");
            return false;
        }

        // 2. 토큰 유효성 검사
        if (!jwtService.isValidToken(accessToken)) {
            System.out.println(" WebSocket handshake 실패: 토큰 유효하지 않음");
            return false;
        }

        // 3. 유효하다면 사용자 정보 저장
        String username = jwtService.getUserName(accessToken);
        String name = jwtService.getName(accessToken);
        String role = jwtService.getUserRole(accessToken);

        System.out.println(" WebSocket handshake 성공: " + username + " (" + role + ")");

        attributes.put("username", username);
        attributes.put("name", name);
        attributes.put("role", role);

        return true;
    }

    // 핸드셰이크 후 특별한 작업은 없음
    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {

    }
}
