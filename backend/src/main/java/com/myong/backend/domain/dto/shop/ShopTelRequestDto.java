package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShopTelRequestDto {

    @NotBlank
    private String tel;
}
