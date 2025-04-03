package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.myong.backend.domain.entity.shop.Notice}
 */
@Value
public class ShopNoticeRequestDto implements Serializable {

    @NotBlank
    String title;

    @NotBlank
    String content;
}