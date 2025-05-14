package com.myong.backend.domain.dto.chating.response;


import com.myong.backend.domain.dto.chating.request.ChatMessageRequestDto;

import com.myong.backend.domain.entity.chating.Message;
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
    private String content;     // 메세지 내용
    private List<String> file;  // 파일 url
    private LocalDateTime sendDate;  // 보낸 시간

    public static ChatMessageResponseDto noFiles(Message message){
        return ChatMessageResponseDto.builder()
                .content(message.getContent())
                .sendDate(message.getSendDate())
                .build();
    }

    public static ChatMessageResponseDto withFiles(Message message){
        return ChatMessageResponseDto.builder()
                .content(message.getContent())
                .sendDate(message.getSendDate())
                .build();
    }

}
