package com.myong.backend.controller;


import com.myong.backend.domain.dto.designer.Api;
import com.myong.backend.domain.dto.designer.ResumeRequestDto;
import com.myong.backend.domain.dto.designer.SignUpRequestDto;
import com.myong.backend.domain.dto.designer.UpdateProfileRequestDto;
import com.myong.backend.domain.dto.email.EmailCheckDto;
import com.myong.backend.domain.dto.email.EmailRequestDto;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.Resume;
import com.myong.backend.service.DesignerService;
import com.myong.backend.service.EmailSendService;
import com.myong.backend.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/designer")
public class DesignerController {

    private final DesignerService designerService;
    private final ResumeService resumeService;


    @PostMapping("/signup")
    //회원가입
    public ResponseEntity<SignUpRequestDto>signup(
            @Valid
            @RequestBody
            SignUpRequestDto request,
            ResumeRequestDto resumeRequestDto) {
        log.info("signup request: {}", request); //디버깅용 로그찍기

        designerService.signUp(request, resumeRequestDto);//서비스에 바디를 넣기

        ResponseEntity<SignUpRequestDto> response = ResponseEntity.status(HttpStatus.OK).body(request);
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

    //이메일 전송 버튼 클릭시
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
            @Valid @RequestBody UpdateProfileRequestDto request
            ){
            log.info("update profile: {}", request);

            Designer updatedesigner = designerService.updateProfile(email, request);
            return ResponseEntity.ok(updatedesigner);
    }

    //디자이너 이력서 수정


    @PostMapping("/resume/update/{email}")
    public ResponseEntity<Resume> updateResume(
            @PathVariable String email,
            @Valid  @RequestBody ResumeRequestDto resumeDto ){
        log.info("update resume: {}", resumeDto);

        Resume resume = resumeService.updateResume(email, resumeDto);
        return ResponseEntity.ok(resume);
    }



    //디자이너 이력서 가져오기
    @GetMapping("/resume/{email}")
    public ResponseEntity<Resume> getResume(@PathVariable String email){
        Resume resume = designerService.getResume(email);
        return ResponseEntity.ok(resume);
    }


}
