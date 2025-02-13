package com.myong.backend.controller;



import com.myong.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

//    @PostMapping("/signin")
//    public TokenInfo login(@RequestBody UserLoginRequestDto userLoginRequestDto){
//        String email = userLoginRequestDto.getEmail();
//        String password = userLoginRequestDto.getPassword();
//        TokenInfo tokenInfo = userService.login(email,password);
//        return tokenInfo;
//    }



}