package com.myong.backend.service;



import com.myong.backend.domain.entity.user.User;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final JwtService jwtService;


    // 카카오 회원가입 로그인 및 로그인 처리
    public ResponseEntity<?> kakaoSignin(String code){
        String accessToken = kakaoGetAccessToken(code).toString();
        if(accessToken == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"인가 어세스토큰을 가져오지 못했습니다.");
        }

        Map<String,Object> userInfo = getUserInfo(accessToken);
        if(userInfo == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"유저 정보를 찾지 가져오지 못했습니다.");
        }

        Map<String,Object> kakao_account =  (Map<String, Object>) userInfo.get("kakao_account");
        String email = (String) kakao_account.get("email");

        User user = userRepository.findByEmail(email).orElse(null);
        // 이미 유저가 회원가입이 되어있음 로그인
        if(user != null){
            // 어세스 토큰생성
            String userAccessToken = jwtService.createAccessToken(user.getEmail(),user.getName(),"USER");
            // 리프레시 토큰저장
            jwtService.saveRedisRefreshToken(user.getName());

            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken"+userAccessToken)
                    .httpOnly(true)
                    .secure(false)
                    .maxAge(60*60)
                    .path("/")
                    .sameSite("Lax")
                    .build();

        }


    }


    // 리다이렉트 로 반환된 인가코드로 어세스토큰 요청
    public Object kakaoGetAccessToken (String code){

        URI uri;

        try{
            uri = new URI(kakaoTokenUri +"?grant_type=authorization_code"+
                    "&client_id="+ kakaoClientId +
                    "&client_secret="+ kakaoClientSecret +
                    "&redirect_uri="+ kakaoRedirectUri +
                    "&code="+code);
        }catch (URISyntaxException e){
            throw new RuntimeException("Invalid URI syntax", e);
        }


        // URIComponentsBuilder : GET 요청을 POST요청 변환
        String requestUrl = UriComponentsBuilder.fromUri(uri).toUriString();

        ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.POST,null, Map.class);

        if(response.getStatusCode() != HttpStatus.OK){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR," 요청 중 에러발생");
        }

        return response.getBody().get("access_token");
    }


    // 어세스토큰으로 유저정보 반환
    public Map<String,Object> getUserInfo(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer " + accessToken );
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(kakaoUserInfoUri,HttpMethod.GET,entity,Map.class);

        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

}