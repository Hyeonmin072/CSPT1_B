package com.myong.backend.domain.dto.shop;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ShopNoticeResponseDto implements Serializable {
    UUID id;
    String title;
    LocalDateTime createDate;
    Boolean importance;
}