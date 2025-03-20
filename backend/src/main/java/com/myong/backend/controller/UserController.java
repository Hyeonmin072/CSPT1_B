package com.myong.backend.controller;



import com.myong.backend.domain.dto.email.EmailCheckDto;
import com.myong.backend.domain.dto.email.EmailRequestDto;
import com.myong.backend.domain.dto.user.ShopDetailsResponseDto;
import com.myong.backend.domain.dto.user.UserHairShopPageResponseDto;
import com.myong.backend.domain.dto.user.UserHomePageResponseDto;
import com.myong.backend.domain.dto.user.UserSignUpDto;
import com.myong.backend.service.EmailSendService;
import com.myong.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "유저 컨트롤러 ")
public class UserController {
    private final UserService userService;
    private final EmailSendService emailSendService;


    @PostMapping("/signup")
//    @Operation(summary = "유저 회원가입")
//    @ApiResponse(responseCode = "200",description = "회원 가입 성공")
//    @ApiResponse(responseCode = "400",description = "회원 가입 실패")
    public ResponseEntity<String> SignUp(
//            @Parameter(required = true, description = "요청")
            @Valid @RequestBody UserSignUpDto userSignUpDto){

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

    // 유저 페이지 헤어샵카테고리
    @GetMapping("/hairshop/{useremail}")
    public ResponseEntity<UserHairShopPageResponseDto> loadHairShopPage(@PathVariable(name = "useremail")String useremail){
        return ResponseEntity.ok(userService.loadHairShopPage(useremail));
    }


    // 유저 홈페이지
    @GetMapping("/homepage/{useremail}")
    public ResponseEntity<UserHomePageResponseDto> loadHomePage(@PathVariable(name = "useremail")String useremail){
        return ResponseEntity.ok(userService.loadHomePage(useremail));
    }


    // 헤어샵 상세보기
    @GetMapping("/shopdetails/{shopemail}")
    public ResponseEntity<ShopDetailsResponseDto> loadHairShopDetailsPage(@PathVariable(name = "shopemail")String shopemail){
        return ResponseEntity.ok(userService.loadHairShopDetailsPage(shopemail));
    }


}