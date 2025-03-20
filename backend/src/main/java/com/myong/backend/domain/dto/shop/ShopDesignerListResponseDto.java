package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopDesignerListResponseDto {

    @NotBlank
    String name; // 디자이너 이름

    @NotBlank
    @Email
    String email;// 디자이너 이메일

    @NotBlank
    String gender;// 디자이너 성별
    
    @NotBlank
    Integer like; // 디자이너 좋아요 개수
}
