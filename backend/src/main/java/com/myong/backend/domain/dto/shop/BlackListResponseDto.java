package com.myong.backend.domain.dto.shop;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BlackListResponseDto {

    @NotBlank
    String userName; // 유저 이름

    @NotBlank
    @Email
    String userEmail; // 유저 이메일

    @NotBlank
    String reason; // 차단 사유
}
