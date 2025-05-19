package com.myong.backend.domain.dto.chatting.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ChattingResponseDto {
    private UUID chatRoomId;
}
