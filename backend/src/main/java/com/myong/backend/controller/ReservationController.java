package com.myong.backend.controller;

import com.myong.backend.domain.dto.reservation.ReservationAcceptRequestDto;
import com.myong.backend.domain.dto.reservation.ReservationCreateRequestDto;
import com.myong.backend.domain.dto.reservation.ReservationInfoResponseDto;
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

}
