package com.myong.backend.domain.dto.menu;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class ShopMenuEditDto {

    @NotBlank
    String shop;

    @NotBlank
    String designer;

    @NotBlank
    String name;

    @NotBlank
    String desc;

    Integer price;

    String estimatedTime;

    @NotBlank
    String common;
}
