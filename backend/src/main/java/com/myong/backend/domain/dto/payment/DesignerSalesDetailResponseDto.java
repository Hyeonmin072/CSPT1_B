package com.myong.backend.domain.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DesignerSalesDetailResponseDto {
    private LocalDateTime paymentTime;
    private String menuName;
    private Long sales;
    private String userName;
    private String shopName;
}
