package com.myong.backend.domain.dto.shop;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link com.myong.backend.domain.entity.shop.Notice}
 */
@Value
@Builder
public class ShopNoticeDetailResponse implements Serializable {
    UUID id;
    String title;
    String content;
    LocalDate createDate;
}