package com.myong.backend.domain.dto.chatting.response;


import com.myong.backend.domain.entity.chatting.Message;
import com.myong.backend.domain.entity.chatting.MessageFile;
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
public class ChatRoomMessageResponseDto {
    private String content;         // 메세지 내용
    private List<String> fileUrls;  // 파일 url들
    private LocalDateTime sendDate; // 보낸 시간
    private String sender;          // 보낸 사람 ex)내가보낸거면 me , 상대방이 보낸거면 partner
    private boolean isRead;         // 읽음 여부


    public static ChatRoomMessageResponseDto from(Message message,String sender ){
        return ChatRoomMessageResponseDto.builder()
                .content(message.getContent())
                .fileUrls(message.getFiles().stream().map(MessageFile::getFileUrl).toList())
                .sendDate(message.getSendDate())
                .sender(sender)
                .isRead(message.isRead())
                .build();

    }
}
