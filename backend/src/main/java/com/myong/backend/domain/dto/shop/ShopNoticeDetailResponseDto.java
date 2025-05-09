package com.myong.backend.domain.dto.shop;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ShopNoticeDetailResponseDto {
    UUID id;
    String title;
    String content;
    LocalDateTime createDate;
    Boolean importance;
}