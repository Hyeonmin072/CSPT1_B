package com.myong.backend.controller;


import com.myong.backend.domain.dto.designer.Api;
import com.myong.backend.domain.dto.designer.SignUpRequest;
import com.myong.backend.domain.dto.designer.UpdateProfileRequest;
import com.myong.backend.domain.dto.email.EmailCheckDto;
import com.myong.backend.domain.dto.email.EmailRequestDto;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.service.DesignerService;
import com.myong.backend.service.EmailSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/designer")
public class DesignerController {

    private final DesignerService designerService;


    @PostMapping("/signup")
    //회원가입
    public Api<SignUpRequest>signup(
            @Valid
            @RequestBody
            Api<SignUpRequest> request){
        log.info("signup request: {}", request); //디버깅용 로그찍기

        var body = request.getData();//request의 데이터를 바디에 담고

        designerService.signUp(body);//서비스에 바디를 넣기

        Api<SignUpRequest> response = Api.<SignUpRequest>builder()
                .resultCode(String.valueOf(HttpStatus.OK.value()))//결과코드가 맞으면 200코드를 반환
                .resultMessage(HttpStatus.OK.getReasonPhrase())//결과코드가 맞으면 ok메세지를 반화
                .data(body)
                .build();

        return response;
    }

    //이메일 중복검사
    @GetMapping("email/check/{email}")
    public ResponseEntity<Boolean> checkedEmailDuplicate(@PathVariable String email){
        log.info("checked email duplicate: {}", email);
        //중복되면 true, 중복안되면 false
        return ResponseEntity.ok(designerService.checkEmailDuplication(email));
    }

    //닉네임 중복검사
    @GetMapping("nickname/{nickname}/exists")
    public ResponseEntity<Boolean> checkedNicknameDuplicate(@PathVariable String nickname){
        log.info("checked nickname duplicate: {}", nickname);
        //중복되면 true, 중복안되면 false
        return ResponseEntity.ok(designerService.checkNicknameDuplication(nickname));
    }

    private final EmailSendService emailSendService;

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

    //디자이너 프로필 불러오기
    @GetMapping("/profile/{email}")
    public ResponseEntity<Designer> profile(@PathVariable String email){
        Designer designer = designerService.getProfile(email);
        return ResponseEntity.ok(designer);
    }

    //디자이너 프로필 수정
    @PostMapping("/profile/update/{email}")
    public ResponseEntity<Designer> updateProfile(
            @PathVariable String email,
            @Valid @RequestBody UpdateProfileRequest request
            ){
            log.info("update profile: {}", request);

            Designer updatedesigner = designerService.updateProfile(email, request);
            return ResponseEntity.ok(updatedesigner);
    }

}
