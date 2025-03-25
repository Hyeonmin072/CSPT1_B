package com.myong.backend.domain.dto.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class CouponRegisterRequestDto {

    @NotBlank
    String name;

    @NotNull
    LocalDate getDate;

    @NotNull
    Integer useDate;

    @NotBlank
    String type;

    @NotNull
    Integer price;

    @NotBlank
    String shopEmail;
}
