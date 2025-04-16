package com.myong.backend.domain.dto.user.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopListData {

    private String shopName; // 가게이름
    private String shopEmail; // 가게이메일
    private String shopTel; // 가게 전화번호
    private String shopDesc; // 가게 소개
    private Double shopRating; // 가게 평점
    private Integer shopReviewCount; // 가게 리뷰 갯수
    private LocalTime shopOpenTime; // 오픈시간
    private LocalTime shopCloseTime; // 마감시간
    private String shopAddress; // 가게주소
    private Integer shopPost; //가게 우편번호
    private Double shopLongitude; // 경도
    private Double shopLatitude; // 위도

}
