package com.myong.backend.domain.dto.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor // ← 생성자 추가!
public class CouponResponseDto {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotNull
    private Integer price;

    @NotNull
    private LocalDate getDate; // 유저가 수령 가능한 날짜

    @NotNull
    private Integer useDate; // 유저가 수령 후 사용 가능한 기간
}
