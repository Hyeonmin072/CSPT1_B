package com.myong.backend.controller;



import com.myong.backend.domain.dto.user.UserSignUpDto;
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


    @PostMapping("/signup")
    public ResponseEntity<String> SignUp(@RequestBody UserSignUpDto userSignUpDto){

        return userService.SingUp(userSignUpDto);

    }




}