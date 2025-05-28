package com.myong.backend.domain.dto.chatting.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myong.backend.domain.entity.chatting.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponseDto {

    private UUID chatRoomId;   // 채팅방 아이디
    private String partnerName; // 상대방 아이디
    private String lastMessage;  // 채팅방 마지막 메세지
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime sendDate;  // 메세지 보낸 시간
    private int unreadCount;    // 안읽은 메세지 갯수

    public static ChatRoomResponseDto from(ChatRoom chatRoom, int unreadCount){
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .partnerName(chatRoom.getDesigner().getNickName())
                .lastMessage(chatRoom.getLastMessage())
                .sendDate(chatRoom.getLastSendDate().atZone(ZoneId.of("UTC")))
                .unreadCount(unreadCount)
                .build();
    }

}
