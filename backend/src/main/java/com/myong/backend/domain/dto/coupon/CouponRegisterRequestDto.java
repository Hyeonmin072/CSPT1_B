package com.myong.backend.domain.dto.coupon;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class CouponRegisterRequestDto {

    @NotBlank
    String name;

    @NotBlank
    Integer getDate;

    @NotBlank
    Integer useDate;

    @NotBlank
    String type;

    @NotBlank
    Long amount;

    @NotBlank
    String shopEmail;
}
