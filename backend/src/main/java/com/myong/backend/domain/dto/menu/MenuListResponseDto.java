package com.myong.backend.domain.dto.menu;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    Integer price;
}
