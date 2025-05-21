package com.myong.backend.domain.dto.chatting.request;

import com.myong.backend.domain.entity.chatting.MessageType;
import com.myong.backend.domain.entity.chatting.SenderType;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageRequestDto (String content, LocalDateTime sendDate, String senderEmail, SenderType senderType, List<String> fileUrls, MessageType messageType){

}
