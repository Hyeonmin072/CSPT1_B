package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ShopDesignerRequestDto {

    @NotBlank
    @Email
    String shopEmail; // 사업자 이메일

    @NotBlank
    @Email
    String designerEmail; // 디자이너 이메일
}
