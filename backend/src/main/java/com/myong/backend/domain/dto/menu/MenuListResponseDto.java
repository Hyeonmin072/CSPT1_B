package com.myong.backend.domain.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;


@Value
@Data
public class MenuListResponseDto {
    @NotBlank
    String id;

    @NotBlank
    String name;

    @NotBlank
    String designerName;

    @NotNull
    Integer price;
}
