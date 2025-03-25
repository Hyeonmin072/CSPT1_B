package com.myong.backend.controller;

import com.myong.backend.domain.dto.reservation.request.ReservationAcceptRequestDto;
import com.myong.backend.domain.dto.reservation.request.ReservationCreateRequestDto;
import com.myong.backend.domain.dto.reservation.response.*;
import com.myong.backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<String> createReservation(@RequestBody ReservationCreateRequestDto requestDto){
        return reservationService.createReservation(requestDto);
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptReservation(@RequestBody ReservationAcceptRequestDto requestDto){
        return reservationService.acceptReservation(requestDto);
    }

    @PostMapping("/refuse")
    public ResponseEntity<String> refuseReservation(@RequestBody ReservationAcceptRequestDto requestDto){
        return reservationService.refuseReservation(requestDto);
    }

    @GetMapping("/{userEmail}")
    public List<ReservationInfoResponseDto> getReservationByUser(@PathVariable("userEmail") String userEmail){
        return reservationService.getReservationByUser(userEmail);
    }


    // 예약 페이지 1번(디자이너 선택)
    @GetMapping("/selectdesigner/{shopemail}")
    public ResponseEntity<List<ReservationPage1ResponseDto>> loadSelectDesignerPage(@PathVariable(name = "shopemail")String shopemail) {
        return ResponseEntity.ok(reservationService.loadSelectDesignerPage(shopemail));
    }


    // 예약 페이지 2번(시간 선택)
    @GetMapping("/selecttime/{designeremail}")
    public ResponseEntity<ReservationPage2ResponseDto> loadSelectTimePage(@PathVariable(name = "designeremail")String designeremail){
        return ResponseEntity.ok(reservationService.loadSelectTimePage(designeremail));
    }


    // 예약 페이지 2번(날짜 별 예약정보 가져오기)
    @GetMapping("selecttime/available-time")
    public ResponseEntity<AvailableTimeResponseDto> getAvailableTime(@RequestParam(name = "designeremail")String designeremail,
                                                                    @RequestParam(name = "day") LocalDate day){
        return ResponseEntity.ok(reservationService.getAvailableTime(designeremail,day));
    }

    // 예약 페이지 3번(메뉴 선택)
    @GetMapping("/selectmenu/{designeremail}")
    public ResponseEntity<SelectMenuResponseDto> loadSelectMenuPage(@PathVariable(name = "designeremail")String designeremail){
        return ResponseEntity.ok(reservationService.loadSelectMenuPage(designeremail));
    }



}
