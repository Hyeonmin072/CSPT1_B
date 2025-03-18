package com.myong.backend.controller;

import com.myong.backend.domain.dto.coupon.CouponListResponseDto;
import com.myong.backend.domain.dto.coupon.CouponRegisterRequestDto;
import com.myong.backend.domain.dto.reservation.ShopReservationRequestDto;
import com.myong.backend.domain.dto.reservation.ShopReservationResponseDto;
import com.myong.backend.domain.dto.shop.ShopDesignerRequestDto;
import com.myong.backend.domain.dto.event.EventListResponseDto;
import com.myong.backend.domain.dto.event.EventRegisterRequestDto;
import com.myong.backend.domain.dto.job.JobPostEditDto;
import com.myong.backend.domain.dto.job.JobPostListResponseDto;
import com.myong.backend.domain.dto.menu.MenuEditDto;
import com.myong.backend.domain.dto.menu.MenuListResponseDto;
import com.myong.backend.domain.dto.shop.*;
import com.myong.backend.service.DesignerService;
import com.myong.backend.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Slf4j
public class ShopController {
    private final ShopService shopService;
    private final DesignerService designerService;

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

    /**
     * 사업자 메뉴 조회
     */
    @GetMapping("/getmenu")
    public ResponseEntity<List<MenuListResponseDto>> getMenu(@Valid @RequestBody ShopEmailRequestDto request) {
        return ResponseEntity.ok(shopService.getMenu(request)); // 성공적으로 로직이 수행될 경우 메뉴 정보 반환
    }

    /**
     * 사업자 메뉴 등록
     */
    @PostMapping("/addmenu")
    public ResponseEntity<String> addMenu(@Valid @RequestBody MenuEditDto request) {
        return ResponseEntity.ok(shopService.addMenu(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 사업자 메뉴 수정
     */
    @PostMapping("/updatemenu")
    public ResponseEntity<String> updateMenu(@Valid @RequestBody MenuEditDto request) {
        return ResponseEntity.ok(shopService.updateMenu(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 사업자 메뉴 삭제f
     */
    @PostMapping("/deletemenu")
    public ResponseEntity<String> deleteMenu(@Valid @RequestBody MenuEditDto request) {
        return ResponseEntity.ok(shopService.deleteMenu(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }
   

    /**
     * 사업자 구인글 목록 조회
     */
    @GetMapping("/getjobposts")
    public ResponseEntity<List<JobPostListResponseDto>> getJobPosts(@Validated @RequestBody ShopEmailRequestDto request) {
        return ResponseEntity.ok(shopService.getJobPosts(request));
    }
    
    /**
     * 사업자 구인글 등록
     */
    @PostMapping("/addjobpost")
    public ResponseEntity<String> addJobPost(@Validated @RequestBody JobPostEditDto request) {
        return ResponseEntity.ok(shopService.addJobPost(request));
    }

    /**
     * 사업자 구인글 수정
     */
    @PostMapping("/updatejobpost")
    public ResponseEntity<String> updateJobPost(@Validated @RequestBody JobPostEditDto request) {
        return ResponseEntity.ok(shopService.updateJobPost(request));
    }

    /**
     * 사업자 구인글 마감
     */
    @PostMapping("/deletejobpost")
    public ResponseEntity<String> deleteJobPost(@Validated @RequestBody JobPostEditDto request) {
        return ResponseEntity.ok(shopService.deleteJobPost(request));
    }

    /**
     * 사업자 소속된 디자이너 목록 조회
     */
    @GetMapping("/designers")
    public ResponseEntity<List<ShopDesignerListResponseDto>> getDesigners(@Validated @RequestBody ShopEmailRequestDto request) {
        return ResponseEntity.ok(shopService.getDesigners(request));
    }

    /**
     * 사업자 소속된 디자이너 상세 조회
     */
    @GetMapping("/designer/detail")
    public ResponseEntity<ShopDesignerDetailResponseDto> getDesigner(@Validated @RequestBody ShopDesignerRequestDto request) {
        return ResponseEntity.ok(designerService.getDesigner(request));
    }

    /**
     * 사업자 디자이너 추가
     */
    @PostMapping("/designer/join")
    public ResponseEntity<String> joinDesigner(@Validated @RequestBody ShopDesignerRequestDto request) {
        return ResponseEntity.ok(shopService.joinDesigner(request));
    }

    /**
     * 사업자 디자이너 삭제
     */
    @PostMapping("/designer/delete")
    public ResponseEntity<String> deleteDesigner(@Validated @RequestBody ShopDesignerRequestDto request) {
        return ResponseEntity.ok(shopService.deleteDesigner(request));
    }

    /**
     * 사업자 블랙리스트 목록 조회
     */
    @GetMapping("/blacklists")
    public ResponseEntity<List<BlackListResponseDto>> getBlackLists(@Validated @RequestBody ShopEmailRequestDto request) {
        return ResponseEntity.ok(shopService.getBlackLists(request));
    }

    /**
     * 사업자 블랙리스트 추가
     */
    @PostMapping("/blacklist/create")
    public ResponseEntity<String> createBlackList(@Validated @RequestBody BlackListRequestDto request) {
        return ResponseEntity.ok(shopService.createBlackList(request));
    }

    /**
     * 사업자 블랙리스트 삭제
     */
    @PostMapping("/blacklist/delete")
    public ResponseEntity<String> deleteBlackList(@Validated @RequestBody List<BlackListRequestDto> requests) {
        return ResponseEntity.ok(shopService.deleteBlackList(requests));
    }

    /**
     * 사업자 예약 관리(조회)
     */
    @GetMapping("/reservations")
    public ResponseEntity<List<ShopReservationResponseDto>> getReservations(@Validated @RequestBody ShopReservationRequestDto request) {
        return ResponseEntity.ok(shopService.getReservations(request));
    }
}
