package com.myong.backend.domain.dto.shop;

import lombok.Getter;

@Getter
public class ShopSignUpRequestDto {
    private String name;
    private String address;
    private Integer post;
    private String tel;
    private String bizId;
    private String email;
    private String password;
}
