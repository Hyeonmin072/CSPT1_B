package com.myong.backend.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class ShopSalesResponseDto {
    private Long totalAmount;
    private Long todayTotalAmount;
    private Map<String, Long> graph;
}
