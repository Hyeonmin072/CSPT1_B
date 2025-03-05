package com.myong.backend.domain.dto.coupon;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    Long amount;

    @NotBlank
    Period getDate;

    @NotBlank
    Period useDate;
}
