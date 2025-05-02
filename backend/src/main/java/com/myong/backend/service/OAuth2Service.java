package com.myong.backend.service;



import com.myong.backend.api.KakaoMapApi;
import com.myong.backend.domain.dto.oauth2.KakaoSigninResponseDto;
import com.myong.backend.domain.dto.oauth2.SocialSignupRequestDto;
import com.myong.backend.domain.entity.user.Grade;
import com.myong.backend.domain.entity.user.MemberShip;
import com.myong.backend.domain.entity.user.SigninType;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.repository.MemberShipRepository;
import com.myong.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final UserRepository userRepository;
    private final KakaoMapApi kakaoMapApi;
    private final MemberShipRepository memberShipRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> SocialSignup(SocialSignupRequestDto requestDto, SigninType signinType){

        String coordinate = kakaoMapApi.getCoordinatesFromAddress(requestDto.getAddress());
        System.out.println("좌표 추출 : "+coordinate);
        String latitude = coordinate.split(" ")[0]; // 위도
        String longitude = coordinate.split(" ")[1]; // 경도

        User user = User.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .pwd(passwordEncoder.encode(""))
                .location(requestDto.getAddress())
                .tel(requestDto.getTel())
                .birthDate(requestDto.getBirthDate())
                .address(requestDto.getAddress())
                .post(requestDto.getPost())
                .gender(requestDto.getGender())
                .latitude(Double.parseDouble(latitude))
                .longitude(Double.parseDouble(longitude))
                .signinType(signinType)
                .build();

        MemberShip memberShip = MemberShip.builder()
                .user(user)
                .grade(Grade.NONE)
                .build();

        userRepository.save(user);
        memberShipRepository.save(memberShip);

        return ResponseEntity.ok("회원 가입에 성공하셨습니다.");
    }




}