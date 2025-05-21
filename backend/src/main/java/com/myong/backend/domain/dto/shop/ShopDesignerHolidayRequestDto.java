package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ShopDesignerHolidayRequestDto {
    @NotNull
    LocalDate holiday; // 디자이너 휴일
}
