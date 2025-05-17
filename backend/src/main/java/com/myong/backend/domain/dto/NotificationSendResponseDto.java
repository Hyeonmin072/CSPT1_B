package com.myong.backend.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NotificationSendResponseDto {
    private String eventId;
    private String content;

    public NotificationSendResponseDto(String eventId, String content) {
        this.eventId = eventId;
        this.content = content;
    }
}
