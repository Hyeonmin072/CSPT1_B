package com.myong.backend.domain.dto.chating.response;


import com.myong.backend.domain.entity.chating.Message;
import com.myong.backend.domain.entity.chating.MessageFile;
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
    private String sender;          // 보낸 사람
    private

    public static ChatRoomMessageResponseDto from(Message message){
        return ChatRoomMessageResponseDto.builder()
                .content(message.getContent())
                .fileUrls(message.getFiles().stream().map(MessageFile::getFileUrl).toList())
                .sendDate(message.getSendDate())
                .sender(message.getSender())
                .build();

    }
}
