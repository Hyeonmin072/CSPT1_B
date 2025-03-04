package com.myong.backend.domain.dto.shop;

import lombok.Data;

import java.util.UUID;

@Data
public class ShopRegisterReviewRequestDto {

    private Double reviewRating;
    private String reviewContent;
    private String reviewImg;
    private String shopEmaill;
    private String designerEmail;
    private String userEmail;
    private UUID reservationId;



}
