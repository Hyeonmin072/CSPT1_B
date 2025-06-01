package com.myong.backend.domain.dto.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CouponRequestDto {

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

}
