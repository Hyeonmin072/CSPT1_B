package com.myong.backend.domain.dto.chatting.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponseDto {

    private UUID chatRoomId;   // 채팅방 아이디
    private String lastMessage;  // 채팅방 마지막 메세지
    private LocalDateTime sendDate;  // 메세지 보낸 시간

}
