package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class ShopBizRequestDto {

    @NotBlank
    String bizId;

    @NotBlank
    String tel;
}
