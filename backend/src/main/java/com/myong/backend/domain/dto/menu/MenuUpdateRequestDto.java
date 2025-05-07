package com.myong.backend.domain.dto.menu;

import com.myong.backend.domain.entity.shop.MenuCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MenuUpdateRequestDto {

    @NotBlank
    List<String> designerEmails;

    @NotBlank
    String id;

    @NotBlank
    String name;

    @NotBlank
    String desc;

    @NotNull
    MenuCategory category;

    @NotNull
    Integer price;

    String estimatedTime;
}
