package com.myong.backend.service;


import com.myong.backend.domain.dto.chating.request.ChatMessageRequestDto;
import com.myong.backend.domain.dto.chating.response.ChatMessageResponseDto;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.chating.ChatRoom;
import com.myong.backend.domain.entity.chating.Message;
import com.myong.backend.exception.ResourceNotFoundException;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.repository.ChatRoomRepository;
import com.myong.backend.repository.MessageRepository;
import com.myong.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FileUploadService fileUploadService;


    /**
     *  TEXT 타입 메세지 저장 및 보내기
     *
     * @param request
     * @param chatRoomId
     * @param requestUser
     *
     * @return ChatMessageResponseDto , content, sendDate,
     */
    public ChatMessageResponseDto sendMessage(ChatMessageRequestDto request, UUID chatRoomId,UserDetailsDto requestUser){

        User user = userRepository.findByEmail(requestUser.getUsername()).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ResourceNotFoundException("해당 채팅방을 찾지 못했습니다."));

        // 메세지 저장
        Message message = Message.saveMessage(request,user.getEmail(),chatRoom);
        messageRepository.save(message);

        return ChatMessageResponseDto.noFiles(message);

    }


    /**
     * file 타입 메세지 저장 및 보내기
     *
     * @param request
     * @param chatRoomId
     * @param file
     * @param requestUser
     * @return
     */
    public ChatMessageResponseDto sendFileMessage(ChatMessageRequestDto request, UUID chatRoomId, MultipartFile file, UserDetailsDto requestUser){

        User user = userRepository.findByEmail(requestUser.getUsername()).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ResourceNotFoundException("해당 채팅방을 찾지 못했습니다."));

        fileUploadService.uploadFile()

    }
}
