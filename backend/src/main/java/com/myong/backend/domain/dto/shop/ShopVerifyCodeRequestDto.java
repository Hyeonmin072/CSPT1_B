package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class ShopVerifyCodeRequestDto {

    @NotBlank
    String tel;

    @NotBlank
    Integer verifyCode;

}