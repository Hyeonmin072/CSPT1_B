package com.myong.backend.domain.dto.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

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

    @NotNull
    Long getDate; // 수령 가능 기간

    @NotNull
    Integer useDate; // 수령 후 사용 가능 기간
}
