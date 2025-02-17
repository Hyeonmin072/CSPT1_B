package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class ShopSignUpRequestDto {

    @NotBlank
    String name;

    @NotBlank
    String address;

    @NotBlank
    Integer post;

    @NotBlank
    String tel;

    @NotBlank
    String bizId;

    @NotBlank
    String email;

    @NotBlank
    String password;
}
