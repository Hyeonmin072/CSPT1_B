package com.myong.backend.domain.dto.menu;

import com.myong.backend.domain.entity.shop.MenuCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Getter
@Builder
public class MenuResponseDto {
    UUID id;

    String name;

    String designerName;

    Integer price;

    MenuCategory category;

    String image;
}
