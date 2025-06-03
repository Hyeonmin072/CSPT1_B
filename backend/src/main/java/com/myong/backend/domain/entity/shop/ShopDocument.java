package com.myong.backend.domain.entity.shop;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "shop")
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopDocument {

    @Id
    private String id;

    private String name;

    private String email;  // 가게 이메일

    private String desc;   // 가게 설명

    private String tel;    // 가게 전화번호

    private String thumbnail;  // 가게 썸네일

    private Double rating;     // 가게 평점

    private Integer reviewCount;  // 가게 리뷰갯수

    private String openTime;   // 가게 오픈시간

    private String closeTime;  // 가게 마감시간

    private String address;       // 가게 주소

    private Integer post;


    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static ShopDocument from (Shop shop){
        return ShopDocument.builder()
                .id(shop.getId().toString())
                .name(shop.getName())
                .email(shop.getEmail())
                .desc(shop.getDesc())
                .tel(shop.getTel())
                .thumbnail(shop.getThumbnail())
                .rating(shop.getRating())
                .reviewCount(shop.getReviewCount())
                .openTime(shop.getOpenTime() != null ?  shop.getOpenTime().format(TIME_FORMATTER) : null)
                .closeTime(shop.getCloseTime() != null ? shop.getCloseTime().format(TIME_FORMATTER) : null)
                .address(shop.getAddress())
                .post(shop.getPost())
                .build();
    }
}
