package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.AssertTrue;
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

    @NotBlank
    String confirmPassword;

    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    private boolean isPwdMatching(){
        return password.equals(confirmPassword);
    };
}
