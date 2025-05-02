package com.myong.backend.domain.dto.menu;

import com.myong.backend.domain.entity.shop.MenuCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class MenuRequestDto {

    @NotBlank
    String designerEmail;

    String id;

    @NotBlank
    String name;

    @NotBlank
    String desc;

    MenuCategory category;

    Integer price;

    String estimatedTime;

    @NotBlank
    String common; // yes or no
}
