package com.myong.backend.domain.dto.reservation;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationInfoResponseDto {

    private LocalDateTime serviceDate;
    private String menu;
    private String payMethod;
    private Long pay;

}
