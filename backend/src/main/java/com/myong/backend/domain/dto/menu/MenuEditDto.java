package com.myong.backend.domain.dto.menu;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class MenuEditDto {

    @NotBlank
    String shopEmail;

    @NotBlank
    String designerEmail;

    @NotBlank
    String id;

    @NotBlank
    String name;

    @NotBlank
    String desc;

    Integer price = 0;

    String estimatedTime = "";

    @NotBlank
    String common; // yes or no
}
