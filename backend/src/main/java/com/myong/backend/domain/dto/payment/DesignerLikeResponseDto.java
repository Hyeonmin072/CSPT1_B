package com.myong.backend.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DesignerLikeResponseDto {
    private String designerName; // 디자이너 이름
    private String designerEmail; // 디자이너 이메일
    private Long IncreasedLikes; // 이번 달 증가한 좋아요 수
    // 디자이너 프로필 사진 향후 추가
}
