package com.myong.backend.service;


import com.myong.backend.api.KakaoMapApi;
import com.myong.backend.domain.dto.user.UserHomePageResponseDto;
import com.myong.backend.domain.dto.user.UserSignUpDto;
import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final KakaoMapApi kakaoMapApi;

    public ResponseEntity<String> SingUp(UserSignUpDto userSignUpDto){

        Optional<User> ouser =  userRepository.findByEmail(userSignUpDto.getEmail());

        // 이메일 중복 체크
        if (checkEmailDuplication(userSignUpDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if(!ouser.isPresent()){

            String result = kakaoMapApi.getCoordinatesFromAddress(userSignUpDto.getAddress());
            System.out.println("위도와 경도:"+result);
            String latitude = result.split(" ")[0];
            String longitude = result.split(" ")[1];

            User user = new User(
                    userSignUpDto.getName(),
                    userSignUpDto.getEmail(),
                    passwordEncoder.encode(userSignUpDto.getPassword()),
                    userSignUpDto.getTel(),
                    userSignUpDto.getBirth(),
                    (userSignUpDto != null && userSignUpDto.getGender().equals("남성") ? Gender.MALE : Gender.FEMALE),
                    userSignUpDto.getAddress(),
                    longitude,
                    latitude
            );

            userRepository.save(user);
            return ResponseEntity.ok("회원 가입에 성공하셨습니다.");
        }
        return ResponseEntity.status(400).body("회원가입에 실패하셨습니다.");

    }

    public ResponseEntity<String> Signout(HttpServletRequest request){
        String authorization = request.getHeader(AUTHORIZATION);

        // 헤더가 없거나 Bearer 토큰이 아닌 경우 다음 필터로 전달
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("잘못된 토큰입니다.");
        }

        try{
            // "Bearer " 이후의 실제 토큰 값 추출
            String token = authorization.split(" ")[1];

            String userName = jwtService.getUserName(token);

            if(redisTemplate.hasKey(userName)){
                redisTemplate.delete(userName);
            }

            SecurityContextHolder.clearContext();

        } catch (Exception e){
            return ResponseEntity.status(400).body("로그아웃 요청 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok("로그아웃에 성공하셨습니다");

    }

    public Boolean checkEmailDuplication(String email) {
        return userRepository.existsByEmail(email);
    }

//    public UserHomePageResponseDto LoadHomePage(String email){
//        Optional<User> findUser = userRepository.findByEmail(email);
//        if(!findUser.isPresent()){
//            throw new NoSuchElementException("해당 유저가 존재하지않습니다");
//        }
//        User user = findUser.get();
//
//        user.getLocation();
//    }

}
