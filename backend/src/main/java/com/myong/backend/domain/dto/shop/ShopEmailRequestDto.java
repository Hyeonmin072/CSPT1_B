package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class ShopEmailRequestDto {

    @Email
    @NotBlank
    String email;
}
