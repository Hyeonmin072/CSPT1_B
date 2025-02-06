package com.myong.backend.controller;

import com.myong.backend.domain.dto.shop.*;
import com.myong.backend.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Slf4j
public class ShopController {
    private final ShopService shopService;


    /**
     * 사업자 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<String> shopSignUp(@RequestBody ShopSignUpRequestDto request) {
        HttpStatus status = shopService.shopSignUp(request);

        if (status == HttpStatus.OK) return ResponseEntity.ok("회원가입이 완료되었습니다.");
        else return ResponseEntity.ok("회원가입 처리 중 오류가 발생했습니다.");
    }


    /**
     * 사업자 전화번호 인증코드 보내기
     */
    @PostMapping("/sendverifycode")
    public SingleMessageSentResponse sendVerifyCode(@RequestBody ShopTelRequestDto request) {
        return shopService.sendOne(request);
    }

    /**
     * 사업자 전화번호 인증코드 확인하기
     */
    @GetMapping("/verifycodecheck")
    public ResponseEntity<String> verifyCode(@RequestBody ShopVerifyCodeRequestDto request) {
        HttpStatus status = shopService.checkVerifyCode(request);

        if (status == HttpStatus.OK) return ResponseEntity.ok("인증이 완료되었습니다.");
        else if (status == HttpStatus.UNAUTHORIZED) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증코드가 일치하지 않습니다.");
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증코드 확인 중 오류가 발생했습니다.");
    }

    /**
     * 사업자번호 인증
     */
    @GetMapping("/checkbiz")
    public ResponseEntity<String> checkBiz(@RequestBody ShopBizRequestDto request) {
        HttpStatus status = shopService.checkBiz(request);

        if (status == HttpStatus.OK) return ResponseEntity.ok("사업자 정보가 확인되었습니다.");
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사업자 정보 확인 중 오류가 발생했습니다.");
    }

    /**
     * 사업자 이메일 중복 확인
     */
    @GetMapping("/checkemail")
    public ResponseEntity<String> checkEmail(@RequestBody ShopEmailRequestDto request) {
        HttpStatus status = shopService.checkEmail(request);

        if (status == HttpStatus.OK) return ResponseEntity.ok("사용가능한 이메일입니다.");
        else if (status == HttpStatus.CONFLICT) return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용중인 이메일 입니다.");
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 중복 확인 중 오류가 발생했습니다.");
    }

}
