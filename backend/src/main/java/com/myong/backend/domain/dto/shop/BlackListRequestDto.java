package com.myong.backend.domain.dto.shop;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BlackListRequestDto {

    @NotBlank
    @Email
    String shopEmail; // 가게 이메일

    @NotBlank
    String userName; // 유저 이름

    @NotBlank
    @Email
    String userEmail; // 유저 이메일

    @NotBlank
    String reason; // 차단 사유
}
