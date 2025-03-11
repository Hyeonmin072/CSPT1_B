package com.myong.backend.controller;



import com.myong.backend.domain.dto.email.EmailCheckDto;
import com.myong.backend.domain.dto.email.EmailRequestDto;
import com.myong.backend.domain.dto.shop.ShopRegisterReviewRequestDto;
import com.myong.backend.domain.dto.user.UserHomePageRequestDto;
import com.myong.backend.domain.dto.user.UserHomePageResponseDto;
import com.myong.backend.domain.dto.user.UserSignUpDto;
import com.myong.backend.service.EmailSendService;
import com.myong.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final EmailSendService emailSendService;


    @PostMapping("/signup")
    public ResponseEntity<String> SignUp(@Valid @RequestBody UserSignUpDto userSignUpDto){

        return userService.SingUp(userSignUpDto);

    }
   

    //Send Email: 이메일 전송 버튼 클릭시
    @PostMapping("/sendemail")
    public Map<String, String> mailSend(
            @RequestBody EmailRequestDto emailRequestDto
    ){
        String code = emailSendService.joinEmail(emailRequestDto.getEmail());
        //response를 Json으로 변환
        Map<String, String> response = new HashMap<>();
        response.put("code", code);

        return response;
    }

    //이메일 인증

    @PostMapping("/verifyemail")
    public String authCheck(@RequestBody @Valid EmailCheckDto emailCheckDto){
        Boolean checked = emailSendService.checkAuthNum(emailCheckDto.getEmail(), emailCheckDto.getAuthNum());
        if(checked){
            return "이메일 인증 성공!!";
        }else {
            throw new NullPointerException("이메일 인증 실패");
        }
    }

    //이메일 중복검사
    @GetMapping("email/check/{email}")
    public ResponseEntity<Boolean> checkedEmailDuplicate(@PathVariable String email){
        log.info("checked email duplicate: {}", email);
        //중복되면 true, 중복안되면 false
        return ResponseEntity.ok(userService.checkEmailDuplication(email));
    }
  
   @PostMapping("/signout")
    public ResponseEntity<String> Signout(HttpServletRequest request){
        System.out.println("컨트롤러에 요청이 넘어옮");
        return userService.Signout(request);
    }

    @PostMapping("/homepage")
    public ResponseEntity<UserHomePageResponseDto> LoadHomePage(@RequestBody
                                                                UserHomePageRequestDto request){

        return ResponseEntity.ok(userService.LoadHomePage(request.getUserEmail()));
    }



}