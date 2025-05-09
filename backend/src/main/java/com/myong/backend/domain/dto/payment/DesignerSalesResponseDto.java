package com.myong.backend.domain.dto.payment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DesignerSalesResponseDto {
    private String designerName; // 디자이너 이름
    private String designerEmail; // 디자이너 이메일
    private Long designerSales; // 디자이너 매출
    private String designerImage; // 디자이너 프로필 사진
}
