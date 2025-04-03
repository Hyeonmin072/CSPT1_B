package com.myong.backend.domain.dto.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class CouponResponseDto {

    @NotBlank
    String id;

    @NotBlank
    String name;

    @NotBlank
    String type;

    @NotNull
    Integer price;

    @NotNull
    LocalDate getDate; // 유저가 수령 가능한 날짜

    @NotNull
    Integer useDate; // 유저가 수령 후 사용 가능한 기간
}
