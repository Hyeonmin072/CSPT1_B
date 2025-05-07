package com.myong.backend.domain.dto.shop;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopMainResponseDto {
    private Long remainReservation; // 남은 예약의 개수
    private Long monthSales; // 이번 달 매출

    private String bestSalesdesignerName; // 매출 우수 디자이너 이름
    private String bestSalesdesignerEmail; // 매출 우수 디자이너 이메일
    private Long sales; // 매출
    // 매출 우수 디자이너 프로필 사진 향후 추가

    private String bestLikedesignerName; // 좋아요 우수 디자이너 이름
    private String bestLikedesignerEmail; // 좋아요 우수 디자이너 이메일
    private Long IncreasedLikes; // 이번 달 증가한 좋아요 수
    // 좋아요 우수 디자이너 프로필 사진 향후 추가
    
    private Double rating; // 가게 평점
    private Integer reviewCount; // 리뷰 개수
}
