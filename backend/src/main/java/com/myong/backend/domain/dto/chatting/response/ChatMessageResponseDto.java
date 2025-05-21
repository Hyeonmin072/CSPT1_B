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
    private String sender;          // 보낸 사람 ex)내가보낸거면 me , 상대방이 보낸거면 partner
    private boolean isRead;         // 읽음 여부

    public static ChatMessageResponseDto noFiles(Message message,String requestEmail){
        boolean isMine = message.getSenderEmail().equals(requestEmail);
        return ChatMessageResponseDto.builder()
                .content(message.getContent())
                .sendDate(message.getSendDate())
                .sender(isMine ? "me" : "partner")
                .isRead(message.isRead())
                .build();
    }

    public static ChatMessageResponseDto withFileUrls(Message message, List<String> fileUrls, String requestEmail){
        boolean isMine = message.getSenderEmail().equals(requestEmail);
        return ChatMessageResponseDto.builder()
                .content(message.getContent())
                .sendDate(message.getSendDate())
                .fileUrls(fileUrls)
                .sender(isMine ? "me" : "partner")
                .isRead(message.isRead())
                .build();
    }

}
