package com.myong.backend.domain.dto.shop;

import lombok.Data;

import java.util.UUID;

@Data
public class ShopRegisterReviewRequestDto {

    private Double reviewRating;
    private String reviewContent;
    private UUID reservationId;



}
