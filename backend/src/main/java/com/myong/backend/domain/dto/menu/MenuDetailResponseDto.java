package com.myong.backend.domain.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class MenuDetailResponseDto {
    @NotBlank
    UUID id;

    @NotBlank
    String name;

    @NotBlank
    String designerName;

    @NotNull
    Integer price;

    @NotBlank
    String desc;

    String image;
}
