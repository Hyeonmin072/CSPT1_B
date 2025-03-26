package com.myong.backend.controller;



import com.myong.backend.domain.dto.email.EmailCheckDto;
import com.myong.backend.domain.dto.email.EmailRequestDto;
import com.myong.backend.domain.dto.reservation.request.ReservationAcceptRequestDto;
import com.myong.backend.domain.dto.reservation.request.ReservationCreateRequestDto;
import com.myong.backend.domain.dto.reservation.response.*;
import com.myong.backend.domain.dto.review.ReviewRemoveRequestDto;
import com.myong.backend.domain.dto.shop.ShopRegisterReviewRequestDto;
import com.myong.backend.domain.dto.user.request.ShopDetailsResponseDto;
import com.myong.backend.domain.dto.user.response.UserHairShopPageResponseDto;
import com.myong.backend.domain.dto.user.response.UserHomePageResponseDto;
import com.myong.backend.domain.dto.user.request.UserSignUpDto;
import com.myong.backend.service.EmailSendService;
import com.myong.backend.service.ReservationService;
import com.myong.backend.service.ReviewService;
import com.myong.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "유저 컨트롤러 ")
public class UserController {

    private final UserService userService;
    private final EmailSendService emailSendService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;


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


    // 예약관련 =====================================================================

    @PostMapping("/reservation/create")
    public ResponseEntity<String> createReservation(@RequestBody ReservationCreateRequestDto requestDto){
        return reservationService.createReservation(requestDto);
    }

    @PostMapping("/reservation/accept")
    public ResponseEntity<String> acceptReservation(@RequestBody ReservationAcceptRequestDto requestDto){
        return reservationService.acceptReservation(requestDto);
    }

    @PostMapping("/reservation/refuse")
    public ResponseEntity<String> refuseReservation(@RequestBody ReservationAcceptRequestDto requestDto){
        return reservationService.refuseReservation(requestDto);
    }

    @GetMapping("/reservation/{userEmail}")
    public List<ReservationInfoResponseDto> getReservationByUser(@PathVariable("userEmail") String userEmail){
        return reservationService.getReservationByUser(userEmail);
    }


    // 예약 페이지 1번(디자이너 선택)
    @GetMapping("/reservation/selectdesigner/{shopemail}")
    public ResponseEntity<List<ReservationPage1ResponseDto>> loadSelectDesignerPage(@PathVariable(name = "shopemail")String shopemail) {
        return ResponseEntity.ok(reservationService.loadSelectDesignerPage(shopemail));
    }


    // 예약 페이지 2번(시간 선택)
    @GetMapping("/reservation/selecttime/{designeremail}")
    public ResponseEntity<ReservationPage2ResponseDto> loadSelectTimePage(@PathVariable(name = "designeremail")String designeremail){
        return ResponseEntity.ok(reservationService.loadSelectTimePage(designeremail));
    }


    // 예약 페이지 2번(날짜 별 예약정보 가져오기)
    @GetMapping("/reservation/selecttime/available-time")
    public ResponseEntity<AvailableTimeResponseDto> getAvailableTime(@RequestParam(name = "designeremail")String designeremail,
                                                                     @RequestParam(name = "day") LocalDate day){
        return ResponseEntity.ok(reservationService.getAvailableTime(designeremail,day));
    }

    // 예약 페이지 3번(메뉴 선택)
    @GetMapping("/reservation/selectmenu/{designeremail}")
    public ResponseEntity<SelectMenuResponseDto> loadSelectMenuPage(@PathVariable(name = "designeremail")String designeremail){
        return ResponseEntity.ok(reservationService.loadSelectMenuPage(designeremail));
    }




    // 리뷰 ==============================================================

    @PostMapping("/review/register")
    public ResponseEntity<String> registerReview(@RequestBody ShopRegisterReviewRequestDto request){
        return ResponseEntity.ok(reviewService.registerReview(request));
    }

    @PostMapping("/review/remove")
    public ResponseEntity<String> removeReview(@RequestBody ReviewRemoveRequestDto request){
        return reviewService.reviewRemove(request);

    }




}