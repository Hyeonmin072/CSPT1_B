package com.myong.backend.controller;


import com.myong.backend.domain.dto.payment.PaymentFailDto;
import com.myong.backend.domain.dto.payment.PaymentHistoryDto;
import com.myong.backend.domain.dto.payment.PaymentSuccessDto;
import com.myong.backend.domain.dto.reservation.response.*;
import com.myong.backend.domain.dto.review.ReviewRemoveRequestDto;
import com.myong.backend.domain.dto.shop.PaymentRequestDto;
import com.myong.backend.domain.dto.shop.PaymentResponseDto;
import com.myong.backend.domain.dto.shop.ShopRegisterReviewRequestDto;
import com.myong.backend.domain.dto.user.data.ShopListData;
import com.myong.backend.domain.dto.user.request.DesignerLikeRequestDto;
import com.myong.backend.domain.dto.user.response.ShopDetailsResponseDto;
import com.myong.backend.domain.dto.user.request.UserSignUpDto;
import com.myong.backend.domain.dto.user.request.UserUpdateLocationRequestDto;
import com.myong.backend.domain.dto.user.response.*;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.service.ReservationService;
import com.myong.backend.service.ReviewService;
import com.myong.backend.service.SearchService;
import com.myong.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "유저 컨트롤러 ")
public class UserController {

    private final UserService userService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;
    private final SearchService searchService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> SignUp(@Valid @RequestBody UserSignUpDto userSignUpDto){
        return userService.SingUp(userSignUpDto);
    }


    //이메일 중복검사
    @GetMapping("/checkemail/{email}")
    public ResponseEntity<Boolean> checkedEmailDuplicate(@PathVariable(name = "email") String email){
        log.info("checked email duplicate: {}", email);
        //중복되면 true, 중복안되면 false
        return ResponseEntity.ok(userService.checkEmailDuplication(email));
    }

    //로그아웃 요청
   @PostMapping("/signout")
    public ResponseEntity<String> Signout(HttpServletResponse response){return userService.Signout(response);}

    // 유저 헤어샵 페이지 컨트롤러 시작 =========================================================


    /*
     *   유저 헤어샵 페이지 로드
     */
    @GetMapping("/hairshop")
    public ResponseEntity<UserHairShopPageResponseDto> loadHairShopPage(){
        return ResponseEntity.ok(userService.loadHairShopPage());
    }

    /*
     *   유저 헤어샵 최신순 버튼
     */
    @GetMapping("/hairshop/sort-newest")
    public ResponseEntity<List<ShopListData>> hairshopSortNewest(){
        return ResponseEntity.ok(userService.hairshopSortNewest());
    }

    /*
     *   유저 헤어샵 검색 기능(가게명, 위치)
     */
    @GetMapping("/hairshop/search")
    public ResponseEntity<List<ShopListData>> searchHairshop(
            @RequestParam(name = "searchText")String searchText
    ) {
      return ResponseEntity.ok(searchService.searchHairShops(searchText));
    }

    // 유저 헤어샵 페이지 컨트롤러 끝 =========================================================

    // 유저 홈페이지
    @GetMapping("/homepage")
    public ResponseEntity<UserHomePageResponseDto> loadHomePage(){
        return ResponseEntity.ok(userService.loadHomePage());
    }

    // 유저 헤더 로딩
    @GetMapping("/loadheader")
    public ResponseEntity<UserHeaderResponseDto> loadHeader(){
        return ResponseEntity.ok(userService.loadHeader());
    }


    // 헤어샵 상세보기
    @GetMapping("/shopdetails/{shopemail}")
    public ResponseEntity<ShopDetailsResponseDto> loadHairShopDetailsPage(@PathVariable(name = "shopemail")String shopemail){
        return ResponseEntity.ok(userService.loadHairShopDetailsPage(shopemail));
    }


    // 프로필 시작 =============================================================

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> loadUserProfilePage(){
        return ResponseEntity.ok(userService.loadUserProfilePage());
    }

    // 프로필 끝 =============================================================


    // 디자이너 카테고리 시작 =============================================================

    /**
     *  좋아요 누른 디자이너 페이지
     */
    @GetMapping("/like-designerpage")
    public ResponseEntity<List<LikeDesignerPageResponseDto>> loadLikeDesignerPage(){
        return ResponseEntity.ok(userService.loadLikeDesignerPage());
    }

    /**
     *   디자이너 페이지
     */
    @GetMapping("/designerpage")
    public ResponseEntity<DesignerPageResponseDto> loadDesignerPage(@AuthenticationPrincipal UserDetailsDto user){
        return ResponseEntity.ok(userService.loadDesignerPage(user));
    }

    /**
     *   디자이너 좋아요 토글 처리
     */
    @PostMapping("/designerlike")
    public ResponseEntity<Boolean> requestLikeForDesigner (@RequestBody DesignerLikeRequestDto request){
        return ResponseEntity.ok(userService.requestLikeForDesigner(request.getDesignerEmail()));
    }

    // 디자이너 카테고리 끝 =============================================================

    // 예약 및 결제 카테고리 시작 =================================================================

    /**
     * 예약 페이지 1번(디자이너 선택)
     */
    @GetMapping("/reservation/selectdesigner/{shopemail}")
    public ResponseEntity<List<ReservationPage1ResponseDto>> loadSelectDesignerPage(@PathVariable(name = "shopemail")String shopemail) {
        return ResponseEntity.ok(reservationService.loadSelectDesignerPage(shopemail));
    }


    /**
     * 예약 페이지 2번(시간 선택)
     */
    @GetMapping("/reservation/selecttime/{designeremail}")
    public ResponseEntity<ReservationPage2ResponseDto> loadSelectTimePage(@PathVariable(name = "designeremail")String designeremail){
        return ResponseEntity.ok(reservationService.loadSelectTimePage(designeremail));
    }

    /**
     * 예약 페이지 2번(날짜 별 예약정보 가져오기)
     */
    @GetMapping("/reservation/selecttime/available-time")
    public ResponseEntity<AvailableTimeResponseDto> getAvailableTime(@RequestParam(name = "designeremail")String designeremail,
                                                                     @RequestParam(name = "day") LocalDate day){
        return ResponseEntity.ok(reservationService.getAvailableTime(designeremail,day));
    }

    /**
     * 예약 페이지 3번(메뉴 선택)
     */
    @GetMapping("/reservation/selectmenu/{designeremail}")
    public ResponseEntity<SelectMenuResponseDto> loadSelectMenuPage(@PathVariable(name = "designeremail")String designeremail){
        return ResponseEntity.ok(reservationService.loadSelectMenuPage(designeremail));
    }

    /**
     * 결제하기 버튼 -> 임시 예약 데이터 및 결제 객체 생성
     */
    @PostMapping("/reservation")
    public ResponseEntity<PaymentResponseDto> createReservation(@Validated @RequestBody PaymentRequestDto request){
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    /**
     * 결제 인증 성공 시 -> 예약 생성
     */
    @GetMapping("/payment/success")
    public ResponseEntity<PaymentSuccessDto> tossPaymentSuccess(@RequestParam String paymentKey, @RequestParam("orderId") String paymentId, @RequestParam Long amount) {
            return ResponseEntity.ok(reservationService.tossPaymentSuccess(paymentKey, paymentId, amount));
    }

    /**
     * 결제 인증 실패 시
     */
    @GetMapping("/payment/fail")
    public ResponseEntity<PaymentFailDto> tossPaymentFail(@RequestParam String code,
                                                          @RequestParam("orderId") String paymentId,
                                                          @RequestParam String message) {
        return ResponseEntity.ok().body(reservationService.tossPaymentFail(code, paymentId, message));
    }

    /**
     * 결제 내역 조회
     */
    @GetMapping("/payment/history")
    public ResponseEntity<List<PaymentHistoryDto>> findAllChargingHistories() {
        return ResponseEntity.ok().body(reservationService.findAllChargingHistories());
    }

    /**
     * 유저의 예약 조회
     */
    @GetMapping("/reservation")
    public List<ReservationInfoResponseDto> getReservationByUser(){
        return reservationService.getReservationByUser();
    }

    // 예약 및 결제 카테고리 끝 =================================================================


    // 리뷰 ==============================================================

    @PostMapping("/review/register")
    public ResponseEntity<String> registerReview(@RequestBody ShopRegisterReviewRequestDto request){
        return ResponseEntity.ok(reviewService.registerReview(request));
    }

    @PostMapping("/review/remove")
    public ResponseEntity<String> removeReview(@RequestBody ReviewRemoveRequestDto request){
        return reviewService.reviewRemove(request);

    }

    // 쿠폰함 =============================================================

    /*
     *   나의 쿠폰함
     */
    @GetMapping("/allcoupons")
    public ResponseEntity<?> getAllCoupons() throws NotFoundException {
        return ResponseEntity.ok(userService.getAllCoupons());
    }

    // 위치  =============================================================

    /*
     *   위치 업데이트
     */
    @PostMapping("/location/update")
    public ResponseEntity<?> updateLocation(@RequestBody UserUpdateLocationRequestDto requestDto){
        return ResponseEntity.ok(userService.updateLocation(requestDto));
    }

    /*
     *   위치 업데이트
     */
    @GetMapping("/location")
    public ResponseEntity<?> getUserLocation(){
        return ResponseEntity.ok(userService.getUserLocation());
    }



}