package com.myong.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String title;
    private String content;
    private LocalDateTime time;
    private String receiverEmail;
}
