package com.myong.backend.domain.dto.chatting.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myong.backend.domain.entity.chatting.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String partnerImage; // 상대방 사진
    private String lastMessage;  // 채팅방 마지막 메세지
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime sendDate;  // 메세지 보낸 시간
    private int unreadCount;    // 안읽은 메세지 갯수

    public static ChatRoomResponseDto fromUser(ChatRoom chatRoom, int unreadCount){
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .partnerName(chatRoom.getDesigner().getNickName())
                .partnerImage(chatRoom.getDesigner().getImage())
                .lastMessage(chatRoom.getLastMessage())
                .sendDate(
                        chatRoom.getLastSendDate() != null
                                ? chatRoom.getLastSendDate().atZone(ZoneId.of("UTC"))
                                : null
                )
                .unreadCount(unreadCount)
                .build();
    }

    public static ChatRoomResponseDto fromDesigner(ChatRoom chatRoom, int unreadCount){
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .partnerName(chatRoom.getUser().getName())
                .partnerImage(chatRoom.getDesigner().getImage())
                .lastMessage(chatRoom.getLastMessage())
                .sendDate(
                        chatRoom.getLastSendDate() != null
                                ? chatRoom.getLastSendDate().atZone(ZoneId.of("UTC"))
                                : null
                )
                .unreadCount(unreadCount)
                .build();
    }

}
