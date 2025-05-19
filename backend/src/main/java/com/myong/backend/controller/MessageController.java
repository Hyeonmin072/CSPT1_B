package com.myong.backend.controller;


import com.myong.backend.domain.dto.chatting.request.ChatMessageRequestDto;
import com.myong.backend.domain.dto.chatting.response.ChatMessageResponseDto;
import com.myong.backend.domain.dto.chatting.response.ChatSaveFilesResponseDto;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final ChattingService chattingService;

    /**
     * 메세지 보내기
     */
    @MessageMapping("/chat/{chatRoomId}")
    @SendTo("/subscribe/chat/{chatRoomId}")
    public ChatMessageResponseDto sendMessage(@Payload ChatMessageRequestDto request,
                                              @DestinationVariable("chatRoomId") UUID chatRoomId,
                                              SimpMessageHeaderAccessor accessor){
        // 파일이 존재할때
        if(request.fileUrls() != null){
            return chattingService.sendFileMessage(request,chatRoomId,accessor);
        }
        // 파일이 존재하지 않을때
        return chattingService.sendMessage(request,chatRoomId,accessor);
    }


    /**
     * 채팅방 파일 업로드
     */
    @PostMapping("/chat/fileupload/{chatRoomId}")
    public ResponseEntity<ChatSaveFilesResponseDto> saveFiles(@RequestPart(value = "file", required = false) List<MultipartFile> file,
                                                              @PathVariable(name = "chatRoomId")UUID chatRoomId){
        return ResponseEntity.ok(chattingService.saveFiles(file,chatRoomId));
    }

}
