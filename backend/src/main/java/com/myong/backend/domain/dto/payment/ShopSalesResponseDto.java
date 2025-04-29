package com.myong.backend.domain.dto.payment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopSalesResponseDto {
    private Long totalAmount;
    private Long todayTotalAmount;
}
