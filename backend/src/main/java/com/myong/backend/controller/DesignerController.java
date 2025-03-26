package com.myong.backend.controller;


import com.myong.backend.domain.dto.designer.*;
import com.myong.backend.domain.dto.designer.SignUpRequestDto;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/designer")
public class DesignerController {

    private final DesignerService designerService;
    private final ResumeService resumeService;


    @PostMapping("/signup")
    //회원가입
    public ResponseEntity<SignUpRequestDto> signup(
            @Valid
            @RequestBody
            SignUpRequestDto request){
        log.info("signup request: {}", request); //디버깅용 로그찍기

        designerService.signUp(request);

        ResponseEntity<SignUpRequestDto> response = ResponseEntity.status(HttpStatus.OK).body(request);

        return response;
    }

    //이메일 중복검사
    @GetMapping("/checkemail/{email}")
    public ResponseEntity<Boolean> checkedEmailDuplicate(@PathVariable(name = "email") String email){
        log.info("checked email duplicate: {}", email);
        //중복되면 true, 중복안되면 false
        return ResponseEntity.ok(designerService.checkEmailDuplication(email));
    }

    //닉네임 중복검사
    @GetMapping("nickname/{nickname}/exists")
    public ResponseEntity<Boolean> checkedNicknameDuplicate(@PathVariable(value = "nickname") String nickname){
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
    public ResponseEntity<ProfileResponseDto> profile(@PathVariable(value = "email") String email){
        ProfileResponseDto responseDto = designerService.getProfile(email);
        return ResponseEntity.ok(responseDto);
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
            @PathVariable(value = "email") String email,
            @Valid  @RequestBody ResumeRequestDto resumeDto ){
        log.info("update resume: {}", resumeDto);

        Resume resume = resumeService.updateResume(email, resumeDto);
        return ResponseEntity.ok(resume);
    }



    //디자이너 이력서 가져오기
    @GetMapping("/resume/{email}")
    public ResponseEntity<ResumeResponseDto> getResume(@PathVariable(value = "email") String email){
        Resume resume = designerService.getResume(email);

        int currentYear = LocalDate.now().getYear();
        int birthYear = Integer.parseInt(resume.getDesigner().getBirth().toString().substring(0, 4));
        int age = currentYear - birthYear;

        ResumeResponseDto resumeResponseDto = ResumeResponseDto.builder()
                .name(resume.getDesigner().getName())
                .tel(resume.getDesigner().getTel())
                .image(resume.getImage())
                .exp(resume.getExp())
                .gender(resume.getDesigner().getGender())
                .age(age)
                .content(resume.getContent())
                .careers(resume.getCareers())
                .certifications(resume.getCertifications())
                .wantedDays(resume.getWantedDays())
                .build();

        return ResponseEntity.ok(resumeResponseDto);
    }

}
