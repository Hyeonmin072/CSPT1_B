package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ShopVerifyCodeRequestDto {

    @NotBlank
    String tel;

    @NotNull
    Integer verifyCode;

}