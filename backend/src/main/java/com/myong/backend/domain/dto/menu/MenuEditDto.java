package com.myong.backend.domain.dto.menu;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Value;

@Data

public class MenuEditDto {

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
