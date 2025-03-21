package com.myong.backend.domain.dto.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.Period;

@Value
public class CouponListResponseDto {

    @NotBlank
    String id;

    @NotBlank
    String name;

    @NotBlank
    String type;

    @NotNull
    Integer price;

    @NotBlank
    Period getDate;

    @NotBlank
    Period useDate;
}
