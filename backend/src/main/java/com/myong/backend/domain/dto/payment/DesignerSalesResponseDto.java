package com.myong.backend.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DesignerSalesResponseDto {
    private String designerName; // 디자이너 이름
    private Long designerSales; // 디자이너 매출
    // 디자이너 프로필 사진 향후 추가
}
