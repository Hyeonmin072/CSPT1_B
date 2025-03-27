package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopDesignerRequestDto {

    @NotBlank
    @Email
    String designerEmail; // 디자이너 이메일
}
