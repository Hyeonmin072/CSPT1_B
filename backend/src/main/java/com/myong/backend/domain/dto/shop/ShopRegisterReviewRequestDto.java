package com.myong.backend.domain.dto.shop;

import lombok.Data;

import java.util.UUID;

@Data
public class ShopRegisterReviewRequestDto {

    private Double reviewRating;
    private String reviewContent;
    private String reviewImg;
    private String shopEmail;
    private String designerEmail;
    private UUID reservationId;



}
