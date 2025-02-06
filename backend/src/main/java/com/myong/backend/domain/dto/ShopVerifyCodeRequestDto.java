package com.myong.backend.domain.dto;

import lombok.Getter;

@Getter
public class ShopVerifyCodeRequestDto {
    private String tel;
    private Integer verifyCode;
}