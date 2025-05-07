package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.myong.backend.domain.entity.shop.Notice}
 */
@Getter
@NoArgsConstructor
public class ShopNoticeRequestDto implements Serializable {

    @NotBlank
    String title;

    @NotBlank
    String content;

    @NotNull
    Boolean importance;
}