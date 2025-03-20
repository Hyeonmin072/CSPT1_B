package com.myong.backend.domain.dto.designer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class CareerRequestDto {
    @NotBlank
    String shopName;

    @NotBlank
    LocalDate joinDate;

    @NotEmpty
    LocalDate outDate;
}
