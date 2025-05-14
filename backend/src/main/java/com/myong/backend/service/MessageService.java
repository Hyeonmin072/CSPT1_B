package com.myong.backend.service;


import com.myong.backend.domain.dto.chating.request.ChatMessageRequestDto;
import com.myong.backend.domain.dto.chating.response.ChatMessageResponseDto;
import com.myong.backend.domain.dto.chating.response.ChatSaveFilesResponseDto;
import com.myong.backend.domain.entity.chating.MessageFile;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.chating.ChatRoom;
import com.myong.backend.domain.entity.chating.Message;
import com.myong.backend.exception.BindException;
import com.myong.backend.exception.ResourceNotFoundException;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.repository.ChatRoomRepository;
import com.myong.backend.repository.MessageFileRepository;
import com.myong.backend.repository.MessageRepository;
import com.myong.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FileUploadService fileUploadService;
    private final MessageFileRepository messageFileRepository;


    /**
     *  TEXT 타입 메세지 저장 및 보내기
     *
     * @param request
     * @param chatRoomId
     * @param requestUser
     *
     * @return ChatMessageResponseDto :: content, sendDate,
     */
    @Transactional
    public ChatMessageResponseDto sendMessage(ChatMessageRequestDto request, UUID chatRoomId, UserDetailsDto requestUser){

        User user = userRepository.findByEmail(requestUser.getUsername()).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ResourceNotFoundException("해당 채팅방을 찾지 못했습니다."));

        // 메세지 저장
        Message message = Message.saveMessage(request,user.getEmail(),chatRoom);

        // 마지막 메세지 , 보낸시간 업데이트
        chatRoom.updateLastMessage(request.content(),request.sendDate());

        return ChatMessageResponseDto.noFiles(message);

    }

    /**
     * 채팅 파일 업로드 처리
     *
     * @param requestFiles
     * @param ChatRoomId
     * @return ChatSaveFilesResponseDto :: fileUrls, messageType
     */
    public ChatSaveFilesResponseDto saveFiles(List<MultipartFile> requestFiles, UUID ChatRoomId){

        // 요청온 파일 업로드 처리 후 담을 url목록
        List<String> requestFileUrls = new ArrayList<>();
        // 파일 타입
        String fileType = requestFiles.get(0).getContentType().split("/")[0];

        for(MultipartFile file : requestFiles){
            // 파일 타입 가져오기  file.getContentType = ex 이미지일때) image/png 반환
            String route = "chatroom" + "/" + ChatRoomId + "/" + fileType + "/";
            requestFileUrls.add(fileUploadService.uploadFile(file,route));
        }
        return ChatSaveFilesResponseDto.builder()
                .fileUrls(requestFileUrls)
                .messageType(fileType.equals("image") ? "IMAGE" : "FILE" )
                .build();
    }


    /**
     * file 타입 메세지 저장 및 보내기
     *
     * @param request
     * @param chatRoomId
     * @param requestUser
     * @return ChatMessageResponseDto :: content, sendDate, 리스트files
     */
    @Transactional
    public ChatMessageResponseDto sendFileMessage(ChatMessageRequestDto request, UUID chatRoomId, UserDetailsDto requestUser){

        if (request.fileUrls() == null || request.fileUrls().isEmpty()) {
            throw new BindException("파일이 첨부되지 않았습니다.");
        }

        User user = userRepository.findByEmail(requestUser.getUsername()).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ResourceNotFoundException("해당 채팅방을 찾지 못했습니다."));

        // 메세지 저장
        Message message = Message.saveFileMessage(request,user.getEmail(),chatRoom,request.messageType());

        // 파일 메세지들 저장
        List<MessageFile> messageFiles = request.fileUrls().stream()
                .map(url -> MessageFile.save(url, message))
                .collect(Collectors.toList());
        messageFileRepository.saveAll(messageFiles);

        // 마지막 메세지 , 보낸 시각 업데이트
        chatRoom.updateLastMessage(request.content()+"+파일",request.sendDate());

        return ChatMessageResponseDto.withFileUrls(message,request.fileUrls());
    }
}
