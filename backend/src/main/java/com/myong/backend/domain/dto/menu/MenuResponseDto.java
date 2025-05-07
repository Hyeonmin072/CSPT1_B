package com.myong.backend.domain.dto.menu;

import com.myong.backend.domain.entity.shop.MenuCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;


@Getter
@AllArgsConstructor
public class MenuResponseDto {
    UUID id;

    String name;

    String designerName;

    Integer price;

    MenuCategory category;
}
