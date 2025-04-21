package com.myong.backend.domain.dto.user.data;

import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.shop.ShopDocument;
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
    private String shopThumbnail; // 가게 썸네일
    private String shopTel; // 가게 전화번호
    private String shopDesc; // 가게 소개
    private Double shopRating; // 가게 평점
    private Integer shopReviewCount; // 가게 리뷰 갯수
    private LocalTime shopOpenTime; // 오픈시간
    private LocalTime shopCloseTime; // 마감시간
    private String shopAddress; // 가게주소
    private Integer shopPost; //가게 우편번호

    public static ShopListData from (Shop shop) {
        return ShopListData.builder()
                .shopName(shop.getName())
                .shopEmail(shop.getEmail())
                .shopThumbnail(shop.getThumbnail())
                .shopTel(shop.getTel())
                .shopDesc(shop.getDesc())
                .shopRating(shop.getRating())
                .shopReviewCount(shop.getReviewCount())
                .shopOpenTime(shop.getOpenTime())
                .shopCloseTime(shop.getCloseTime())
                .shopAddress(shop.getAddress())
                .shopPost(shop.getPost())
                .build();
    }

    public static ShopListData searchFrom (ShopDocument shop) {
        return ShopListData.builder()
                .shopName(shop.getName())
                .shopEmail(shop.getEmail())
                .shopThumbnail(shop.getThumbnail())
                .shopTel(shop.getTel())
                .shopDesc(shop.getDesc())
                .shopRating(shop.getRating())
                .shopReviewCount(shop.getReviewCount())
                .shopOpenTime(shop.getOpenTime())
                .shopCloseTime(shop.getCloseTime())
                .shopAddress(shop.getAddress())
                .shopPost(shop.getPost())
                .build();
    }

}
