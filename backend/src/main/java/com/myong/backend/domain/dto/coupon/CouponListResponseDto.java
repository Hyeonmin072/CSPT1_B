package com.myong.backend.domain.dto.coupon;

import com.myong.backend.domain.entity.user.DiscountType;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.time.Period;

@Value
public class CouponListResponseDto {

    @NotBlank
    String name;

    @NotBlank
    DiscountType type;

    @NotBlank
    Long amount;

    @NotBlank
    Period getDate;

    @NotBlank
    Period useDate;
}
