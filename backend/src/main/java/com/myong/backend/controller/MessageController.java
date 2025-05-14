package com.myong.backend.controller;


import com.myong.backend.domain.dto.chating.request.ChatMessageRequestDto;
import com.myong.backend.domain.dto.chating.response.ChatMessageResponseDto;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 메세지 보내기
     *
     * @param request
     * @param chatRoomId
     * @param file
     * @param user
     * @return
     */
    @MessageMapping("/chat-{chatRoomId}")
    @SendTo("/subscribe/chat-{chatRoomId}")
    public ChatMessageResponseDto sendMessage(@RequestBody ChatMessageRequestDto request,
                                              @DestinationVariable UUID chatRoomId,
                                              @RequestPart(value = "file", required = false)MultipartFile file,
                                              @AuthenticationPrincipal UserDetailsDto user){
        // 파일이 존재할때
        if(file == null){
            return messageService.sendFileMessage(request,chatRoomId,file,user);
        }
        // 파일이 존재하지 않을때
        return MessageService.sendMessage(request,user);
    }

}
