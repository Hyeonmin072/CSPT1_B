package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ShopProfileRequestDto {
    @NotBlank
    String name;

    @NotBlank
    String address;

    @NotNull
    Integer post;

    @NotBlank
    String tel;

    @NotBlank
    String pwd;

    String newPwd;

    String newPwdConfirm;

    String desc;

    String open;

    String close;

    String regularHoliday;
}
