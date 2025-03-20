package com.myong.backend.domain.dto.designer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerRequestDto {
    @NotBlank
    String shopName;

    @NotNull
    String joinDate;

    @NotEmpty
    String outDate;
}
