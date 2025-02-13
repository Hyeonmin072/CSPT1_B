package com.myong.backend.service;


import com.myong.backend.domain.dto.user.UserSignUpDto;
import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<String> SingUp(UserSignUpDto userSignUpDto){

        Optional<User> ouser =  userRepository.findByEmail(userSignUpDto.getEmail());

        if(!ouser.isPresent()){
            User user = new User(
                    userSignUpDto.getName(),
                    userSignUpDto.getEmail(),
                    passwordEncoder.encode(userSignUpDto.getPassword()),
                    userSignUpDto.getTel(),
                    userSignUpDto.getBirth(),
                    (userSignUpDto != null && userSignUpDto.getGender().equals("남성") ? Gender.MALE : Gender.FEMALE),
                    userSignUpDto.getAddress()
            );

            userRepository.save(user);
            return ResponseEntity.ok("회원 가입에 성공하셨습니다.");
        }
        return ResponseEntity.status(400).body("회원가입에 실패하셨습니다.");

    }



}
