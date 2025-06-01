package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Getter
@NoArgsConstructor
public class ShopNoticeRequestDto {

    @NotBlank
    String title;

    @NotBlank
    String content;

    @NotNull
    Boolean importance;
}