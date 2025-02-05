package com.myong.backend.controller;


import com.myong.backend.domain.dto.TokenInfo;
import com.myong.backend.domain.dto.UserLoginRequestDto;
import com.myong.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/singin")
    public TokenInfo login(@RequestBody UserLoginRequestDto userLoginRequestDto){
        String email = userLoginRequestDto.getEmail();
        String password = userLoginRequestDto.getPassword();
        TokenInfo tokenInfo = userService.login(email,password);
        return tokenInfo;
    }
}
