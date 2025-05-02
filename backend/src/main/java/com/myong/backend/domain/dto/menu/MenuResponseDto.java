package com.myong.backend.domain.dto.menu;

import com.myong.backend.domain.entity.shop.MenuCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class MenuResponseDto {
    @NotBlank
    String id;

    @NotBlank
    String name;

    @NotBlank
    String designerName;

    @NotNull
    Integer price;

    MenuCategory category;
}
