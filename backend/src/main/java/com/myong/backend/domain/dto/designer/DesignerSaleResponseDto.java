package com.myong.backend.domain.dto.designer;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class DesignerSaleResponseDto {
    private Long totalAmount;          // 총 매출
    private Long todayAmount;          // 오늘 매출
    private Map<String, Long> graph;   // 기간별 매출 그래프
}
