package com.myong.backend.domain.dto.shop;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.myong.backend.domain.entity.shop.Notice}
 */
@Value
@Builder
public class ShopNoticeResponseDto implements Serializable {
    UUID id;
    String title;
    LocalDateTime createDate;
}