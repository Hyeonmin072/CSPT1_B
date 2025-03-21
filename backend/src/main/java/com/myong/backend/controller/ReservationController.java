package com.myong.backend.controller;

import com.myong.backend.domain.dto.reservation.request.ReservationAcceptRequestDto;
import com.myong.backend.domain.dto.reservation.request.ReservationCreateRequestDto;
import com.myong.backend.domain.dto.reservation.response.ReservationInfoResponseDto;
import com.myong.backend.domain.dto.reservation.response.ReservationPage1ResponseDto;
import com.myong.backend.domain.dto.reservation.response.ReservationPage2ResponseDto;
import com.myong.backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/reservationpage1/{shopemail}")
    public ResponseEntity<List<ReservationPage1ResponseDto>> loadReservationPage1(@PathVariable(name = "shopemail")String shopemail) {
        return ResponseEntity.ok(reservationService.loadReservationPage1(shopemail));
    }


    @GetMapping("/reservationpage2/{designeremail}")
    public ResponseEntity<ReservationPage2ResponseDto> loadReservationPage2(@PathVariable(name = "designeremail")String designeremail){
        return ResponseEntity.ok(reservationService);
    }

}
