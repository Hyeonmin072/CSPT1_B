package com.myong.backend.controller;

import com.myong.backend.domain.dto.reservation.ReservationRequestDto;
import com.myong.backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/create")
    public ResponseEntity<String> createReservation(@RequestBody ReservationRequestDto requestDto){
        return reservationService.createReservation(requestDto);
    }
}
