package com.myong.backend.domain.dto.menu;

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

    Integer price;

    String estimatedTime;

    @NotBlank
    String common; // yes or no
}
