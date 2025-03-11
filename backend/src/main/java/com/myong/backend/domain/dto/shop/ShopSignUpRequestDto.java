package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ShopSignUpRequestDto {

    @NotBlank
    String name;

    @NotBlank
    String address;

    @NotNull
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
