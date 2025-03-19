package com.myong.backend.domain.dto.designer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class CertificationRequestDto {
    @NotBlank
    private String name;
}
