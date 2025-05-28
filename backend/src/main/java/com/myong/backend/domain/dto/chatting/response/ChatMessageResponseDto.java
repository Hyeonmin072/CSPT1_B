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
    private String chatRoomId;      // 채팅방 아이디
    private String messageId;       // 메세지 아이디
    private String content;         // 메세지 내용
    private List<String> fileUrls;  // 파일 url
    private LocalDateTime sendDate; // 보낸 시간
    private String sender;          // 보낸 사람 이메일
    private String senderType;      // 보낸 사람 타입
    private boolean isRead;         // 읽음 여부

    public static ChatMessageResponseDto noFiles(Message message,String requestEmail,String requestRole){
        return ChatMessageResponseDto.builder()
                .chatRoomId(message.getChatRoom().getId().toString())
                .messageId(message.getId().toString())
                .content(message.getContent())
                .sendDate(message.getSendDate())
                .sender(requestEmail)
                .senderType(requestRole)
                .isRead(message.isRead())
                .build();
    }

    public static ChatMessageResponseDto withFileUrls(Message message, List<String> fileUrls, String requestEmail,String requestRole){
        return ChatMessageResponseDto.builder()
                .chatRoomId(message.getChatRoom().getId().toString())
                .messageId(message.getId().toString())
                .content(message.getContent())
                .sendDate(message.getSendDate())
                .fileUrls(fileUrls)
                .sender(requestEmail)
                .senderType(requestRole)
                .isRead(message.isRead())
                .build();
    }

}
