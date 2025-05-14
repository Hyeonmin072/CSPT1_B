package com.myong.backend.domain.dto.chating.request;

import com.myong.backend.domain.entity.chating.MessageType;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageRequestDto (String content, LocalDateTime sendDate, List<String> fileUrls, MessageType messageType){

}
