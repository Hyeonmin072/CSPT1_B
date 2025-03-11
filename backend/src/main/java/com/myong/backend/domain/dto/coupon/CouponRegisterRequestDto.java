package com.myong.backend.domain.dto.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class CouponRegisterRequestDto {

    @NotBlank
    String name;

    @NotNull
    Integer getDate;

    @NotNull
    Integer useDate;

    @NotBlank
    String type;

    @NotNull
    Long amount;

    @NotBlank
    String shopEmail;
}
