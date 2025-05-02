package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ShopDesignerHolidayRequestDto {

    @NotBlank
    @Email
    String shopEmail; // 사업자 이메일

    @NotBlank
    @Email
    String designerEmail; // 디자이너 이메일

    @NotNull
    LocalDate holiday; // 디자이너 휴일
}
