package com.myong.backend.domain.dto.shop;

import lombok.Getter;

@Getter
public class ShopVerifyCodeRequestDto {
    private String tel;
    private Integer verifyCode;
}