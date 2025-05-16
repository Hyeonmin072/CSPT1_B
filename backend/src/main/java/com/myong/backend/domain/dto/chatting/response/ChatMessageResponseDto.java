package com.myong.backend.domain.dto.chatting.response;


import com.myong.backend.domain.entity.chatting.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private String content;         // 메세지 내용
    private List<String> fileUrls;  // 파일 url
    private LocalDateTime sendDate; // 보낸 시간
    private boolean isRead;         // 읽음 여부

    public static ChatMessageResponseDto noFiles(Message message){
        return ChatMessageResponseDto.builder()
                .content(message.getContent())
                .sendDate(message.getSendDate())
                .isRead(message.isRead())
                .build();
    }

    public static ChatMessageResponseDto withFileUrls(Message message, List<String> fileUrls){
        return ChatMessageResponseDto.builder()
                .content(message.getContent())
                .sendDate(message.getSendDate())
                .fileUrls(fileUrls)
                .isRead(message.isRead())
                .build();
    }

}
