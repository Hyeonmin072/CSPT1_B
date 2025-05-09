package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShopDesignerRequestDto {

    @NotBlank
    @Email
    String designerEmail; // 디자이너 이메일
}
