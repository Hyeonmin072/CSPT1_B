package com.myong.backend.domain.dto.chatting.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatUserInfoResponseDto {
    private String email;
    private String userType;
}
