package com.myong.backend.domain.dto.shop;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopMainResponseDto {
    private Long remainReservation; // 남은 예약의 개수
    private Long monthSales; // 이번 달 매출

    private String bestSalesDesignerName; // 매출 우수 디자이너 이름
    private String bestSalesDesignerEmail; // 매출 우수 디자이너 이메일
    private String bestSalesDesignerImage; // 매출 우수 디자이너의 프로필 사진
    private Long sales; // 매출 우수 디자이너의 매출

    private String bestLikedesignerName; // 좋아요 우수 디자이너 이름
    private String bestLikedesignerEmail; // 좋아요 우수 디자이너 이메일
    private String bestLikedesignerImage; // 좋아요 우수 디자이너의 프로필 사진
    private Long IncreasedLikes; // 좋아요 우수 디자이너의 이번 달 증가한 좋아요 수

    private String shopName; // 가게 이름
    private Double rating; // 가게 평점
    private Integer reviewCount; // 리뷰 개수
}
