package com.myong.backend.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DesignerSalesDetailResponseDto {
    private LocalDateTime paymentTime;
    private String menuName;
    private Long integer;
    private String userName;
}
