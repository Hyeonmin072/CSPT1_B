package com.myong.backend.domain.dto.chatting.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSaveFilesResponseDto {
    private List<String> fileUrls;
    private String messageType;
}
