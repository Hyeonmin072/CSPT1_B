package com.myong.backend.domain.dto.user.List;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ShopListDto {

    private String name; // 가게이름
    private String email; // 가게이메일
    private String tel; // 가게 전화번호
    private String desc; // 가게 소개
    private Double rating; // 가게 평점
    private Integer reviewCount; // 가게 리뷰 갯수
    private LocalTime openTime; // 오픈시간
    private LocalTime closeTime; // 마감시간
    private String address; // 가게주소
    private Integer post; //가게 우편번호
    private Double longitude; // 경도
    private Double latitude; // 위도

}
