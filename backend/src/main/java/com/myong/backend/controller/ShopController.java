package com.myong.backend.controller;

import com.myong.backend.domain.dto.coupon.CouponListResponseDto;
import com.myong.backend.domain.dto.coupon.CouponRegisterRequestDto;
import com.myong.backend.domain.dto.event.EventListResponseDto;
import com.myong.backend.domain.dto.event.EventRegisterRequestDto;
import com.myong.backend.domain.dto.shop.*;
import com.myong.backend.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> shopSignUp(@Valid @RequestBody ShopSignUpRequestDto request) {
        return ResponseEntity.ok(shopService.shopSignUp(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }


    /**
     * 사업자 전화번호 인증코드 보내기
     */
    @PostMapping("/sendverifycode")
    public SingleMessageSentResponse sendVerifyCode(@Valid @RequestBody ShopTelRequestDto request) {
        return shopService.sendOne(request); // 성공적으로 로직이 수행될 경우 정보 반환
    }

    /**
     * 사업자 전화번호 인증코드 확인하기
     */
    @GetMapping("/verifycodecheck")
    public ResponseEntity<String> verifyCode(@Valid @RequestBody ShopVerifyCodeRequestDto request) {
        return ResponseEntity.ok(shopService.checkVerifyCode(request)); // 성공적으로 로직이 수행될 경우 성공 구문 반환
    }

    /**
     * 사업자번호 인증
     */
    @GetMapping("/checkbiz")
    public ResponseEntity<String> checkBiz(@Valid @RequestBody ShopBizRequestDto request) {
        return ResponseEntity.ok(shopService.checkBiz(request)); // 성공적으로 로직이 수행될 경우 성공 구문 반환
    }


    /**
     * 사업자 이메일 중복 확인
     */
    @GetMapping("/checkemail")
    public ResponseEntity<String> checkEmail(@Valid @RequestBody ShopEmailRequestDto request) {
        return ResponseEntity.ok(shopService.checkEmail(request)); // 성공적으로 로직이 수행될 경우 성공 구문 반환
    }

    /**
     * 등록한 쿠폰 조회
     */
    @GetMapping("/getcoupons")
    public ResponseEntity<List<CouponListResponseDto>> getCoupons(@Valid @RequestBody ShopEmailRequestDto request) {
        return ResponseEntity.ok(shopService.getCoupons(request)); // 성공적으로 로직이 수행될 경우 쿠폰 목록 반환
    }

    /**
     * 쿠폰 등록
     */
    @PostMapping("/addcoupon")
    public ResponseEntity<String> addCoupon(@Valid @RequestBody CouponRegisterRequestDto request) {
        return ResponseEntity.ok(shopService.addCoupon(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 등록한 이벤트 조회
     */
    @GetMapping("/getevents")
    public ResponseEntity<List<EventListResponseDto>> getEvents(@Valid @RequestBody ShopEmailRequestDto request) {
        return ResponseEntity.ok(shopService.getEvents(request)); // 성공적으로 로직이 수행될 경우 이벤트 목록 반환
    }

    /**
     * 이벤트 등록
     */
    @PostMapping("/addevent")
    public ResponseEntity<String> addEvent(@Valid @RequestBody EventRegisterRequestDto request) {
        return ResponseEntity.ok(shopService.addEvent(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 사업자 프로필 조회
     */
    @GetMapping("/profile")
    public ResponseEntity<ShopProfileResponseDto> getProfile(@Valid @RequestBody ShopEmailRequestDto request) {
        return ResponseEntity.ok(shopService.getProfile(request)); // 성공적으로 로직이 수행될 경우 프로필 정보 반환
    }

    /**
     * 사업자 프로필 수정
     */
    @PostMapping("/profile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody ShopProfileRequestDto request) {
        return ResponseEntity.ok(shopService.updateProflie(request)); // 성공적으로 로직이 수행될 경우 프로필 정보 반환
    }
}
