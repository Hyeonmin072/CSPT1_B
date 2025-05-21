package com.myong.backend.domain.dto.shop;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlackListRequestDto {
    @NotBlank
    @Email
    String userEmail; // 유저 이메일

    @NotBlank
    String reason; // 차단 사유
}
