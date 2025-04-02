package com.myong.backend.service;



import com.myong.backend.api.KakaoMapApi;
import com.myong.backend.domain.dto.oauth2.KakaoSigninResponseDto;
import com.myong.backend.domain.dto.oauth2.KakaoSignupRequestDto;
import com.myong.backend.domain.entity.user.Grade;
import com.myong.backend.domain.entity.user.MemberShip;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.repository.MemberShipRepository;
import com.myong.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
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
    private final KakaoMapApi kakaoMapApi;
    private final MemberShipRepository memberShipRepository;


    // 카카오 회원가입 로그인 및 로그인 처리
    public ResponseEntity<?> kakaoSignin(String code){
        Object tokenObj = kakaoGetAccessToken(code);
        if (tokenObj == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인가 어세스토큰을 가져오지 못했습니다.");
        }
        String accessToken = tokenObj.toString();

        Map<String,Object> userInfo = getUserInfo(accessToken);
        if(userInfo == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"유저 정보를 찾지 가져오지 못했습니다.");
        }

        // 이메일 추출
        Map<String,Object> kakao_account =  (Map<String, Object>) userInfo.get("kakao_account");
        String email = (String) kakao_account.get("email");
        System.out.println("카카오 이메일 추출 : "+email);

        User user = userRepository.findByEmail(email).orElse(null);

        // 닉네임 추출
        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
        String nickname = (String) properties.get("nickname");
        System.out.println("카카오 닉네임 추출 : "+nickname);

        // 최초 로그인은 회원가입 폼 을 위한 리스폰스 반환
        if(user == null){
            KakaoSigninResponseDto kakaoSigninResponseDto = KakaoSigninResponseDto.builder()
                    .email(email)
                    .nickname(nickname)
                    .status("NEW_USER")
                    .build();
            return ResponseEntity.ok().body(kakaoSigninResponseDto);
        }

        // 가입이 되어있으면 로그인
        ResponseCookie accessTokenCookie = getAccessTokenCookie(user.getEmail(),user.getName(),"USER");

        KakaoSigninResponseDto kakaoSigninResponseDto = KakaoSigninResponseDto.builder()
                .status("EXISTING_USER")
                .build();

        return ResponseEntity.ok().
                header(HttpHeaders.SET_COOKIE,accessTokenCookie.toString())
                .body(kakaoSigninResponseDto);

    }

    public ResponseEntity<?> kakaoSignup(KakaoSignupRequestDto requestDto){

        String coordinate = kakaoMapApi.getCoordinatesFromAddress(requestDto.getAddress());
        System.out.println("좌표 추출 : "+coordinate);
        String latitude = coordinate.split(" ")[0]; // 위도
        String longitude = coordinate.split(" ")[1]; // 경도


        User user = User.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .pwd("")
                .location(requestDto.getAddress())
                .tel(requestDto.getTel())
                .birthDate(requestDto.getBirthDate())
                .address(requestDto.getAddress())
                .post(requestDto.getPost())
                .gender(requestDto.getGender())
                .latitude(Double.parseDouble(latitude))
                .longitude(Double.parseDouble(longitude))
                .build();

        MemberShip memberShip = MemberShip.builder()
                .user(user)
                .grade(Grade.NONE)
                .build();

        userRepository.save(user);
        memberShipRepository.save(memberShip);

        return ResponseEntity.ok("회원 가입에 성공하셨습니다.");
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


    // 쿠키 생성
    public ResponseCookie getAccessTokenCookie (String email, String name, String role){
        // 어세스 토큰생성
        String userAccessToken = jwtService.createAccessToken(email,name,role);
        // 리프레시 토큰저장
        jwtService.saveRedisRefreshToken(email);

        return ResponseCookie.from("accessToken", userAccessToken)
                .httpOnly(true)
                .secure(false)
                .maxAge(60*60)
                .path("/")
                .sameSite("Lax")
                .build();

    }


}