package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class ShopProfileResponseDto {

    @NotBlank
    String id;

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

    String open;

    String close;

    String regularHoliday;
}
