package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShopVerifyCodeRequestDto {

    @NotBlank
    String tel;

    @NotNull
    Integer verifyCode;

}