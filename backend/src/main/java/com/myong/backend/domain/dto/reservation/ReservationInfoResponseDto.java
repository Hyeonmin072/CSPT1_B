package com.myong.backend.domain.dto.reservation;


import com.myong.backend.domain.entity.business.PaymentMethod;
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
    private String payMethod;
    private Integer price;
    private String status;


}
