package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ShopProfileResponseDto {

    @NotBlank
    String name;

    @NotBlank
    String addres;

    @NotNull
    Integer post;

    @NotBlank
    String tel;

    @NotBlank
    String pwd;

    String desc;

    String open;

    String close;

    String regularHoliday;
}
