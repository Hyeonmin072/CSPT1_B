package com.myong.backend.oauth2;

import com.myong.backend.domain.entity.user.User;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.oauth2.exception.CustomOAuth2AuthenticationException;
import com.myong.backend.oauth2.exception.UnsupportedOAuth2ProviderException;
import com.myong.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{

        System.out.println("getClientRegistration: "+userRequest.getClientRegistration());
        System.out.println("getAccessToken: "+userRequest.getAccessToken());
        System.out.println("getAttributes: "+super.loadUser(userRequest).getAttributes());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        return switch (registrationId){
            case "kakao" -> kakaoSignin(oAuth2User);
            case "google" -> googleSignin(oAuth2User);
            default -> throw new UnsupportedOAuth2ProviderException(registrationId);
        };

    }

    // 카카오톡 로그인 진행
    private OAuth2User kakaoSignin(OAuth2User oAuth2User){
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if (attributes == null) {
            throw new OAuth2AuthenticationException("OAuth2 attributes are null");
        }

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            throw new OAuth2AuthenticationException("Kakao account information is missing");
        }

        Map<String,Object> properties = (Map<String,Object>) attributes.get("properties");
        if (properties == null) {
            throw new OAuth2AuthenticationException("Kakao properties are missing");
        }

        String email = (String) kakaoAccount.get("email");
        String name = (String) properties.get("nickname");

        Optional<User> existingUser = userRepository.findByEmail(email);
        // 최초 로그인 처리
        if(existingUser.isEmpty()){
            throw new CustomOAuth2AuthenticationException(email,name);
        }

        User user = existingUser.get();

        return new UserDetailsDto(
                user.getEmail(),
                "USER",
                user.getName(),
                attributes
        );
    }


    // 구글 로그인 진행
    private OAuth2User googleSignin(OAuth2User oAuth2User){
        Map<String, Object> attributes = oAuth2User.getAttributes();


        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");


        Optional<User> existingUser = userRepository.findByEmail(email);

        // 최초 로그인 처리
        if (existingUser.isEmpty()) {
            throw new CustomOAuth2AuthenticationException(email,name);
        }

        User user = existingUser.get();

        return new UserDetailsDto(
                user.getEmail(),
                "USER",
                user.getName(),
                attributes
        );
    }


}
