package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ShopDesignerResponseDto {

    @NotBlank
    String name; // 디자이너 이름

    @NotBlank
    @Email
    String email;// 디자이너 이메일

    @NotBlank
    String gender;// 디자이너 성별
    
    @NotNull
    Integer like; // 디자이너 좋아요 개수
}
