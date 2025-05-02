package com.myong.backend.controller;


import com.myong.backend.domain.dto.email.EmailCheckDto;
import com.myong.backend.domain.dto.email.EmailRequestDto;
import com.myong.backend.service.EmailSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailSendService emailSendService;


    //Send Email: 이메일 전송 버튼 클릭시
    @PostMapping("/send")
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
    @PostMapping("/verify")
    public String authCheck(@RequestBody @Valid EmailCheckDto emailCheckDto){
        Boolean checked = emailSendService.checkAuthNum(emailCheckDto.getEmail(), emailCheckDto.getAuthNum());
        if(checked){
            return "이메일 인증 성공!!";
        }else {
            throw new NullPointerException("이메일 인증 실패");
        }
    }


}
