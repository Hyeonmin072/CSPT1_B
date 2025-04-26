package com.myong.backend.domain.dto.reservation.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ReservationInfoResponseDto {

    private LocalDateTime serviceDate;
    private String menu;
    private String shop;
    private String designer;
    private Integer price;
}
