package com.myong.backend.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class DesignerSalesDetailResponseDto {
    private LocalDateTime paymentTime;
    private String menuName;
    private Long integer;
    private String userName;
}
