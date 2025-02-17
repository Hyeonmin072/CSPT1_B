package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.time.LocalTime;

@Value
public class ShopProfileResponseDto {
    @NotBlank
    String name;

    @NotBlank
    String addres;

    @NotBlank
    Integer post;

    @NotBlank
    String tel;

    @NotBlank
    String pwd;

    String desc;

    LocalTime open;

    LocalTime close;

    String regularHoliday;
}
