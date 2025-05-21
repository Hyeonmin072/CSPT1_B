package com.myong.backend.service;


import com.myong.backend.domain.dto.chatting.request.ChatMessageRequestDto;
import com.myong.backend.domain.dto.chatting.response.ChatMessageResponseDto;
import com.myong.backend.domain.dto.chatting.response.ChatSaveFilesResponseDto;
import com.myong.backend.domain.entity.chatting.MessageFile;
import com.myong.backend.domain.entity.chatting.SenderType;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.chatting.ChatRoom;
import com.myong.backend.domain.entity.chatting.Message;
import com.myong.backend.exception.BindException;
import com.myong.backend.exception.ResourceNotFoundException;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChattingService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FileUploadService fileUploadService;
    private final MessageFileRepository messageFileRepository;
    private final DesignerRepository designerRepository;
    private final ChattingOnlineService chattingOnlineService;
    private final SimpMessagingTemplate messagingTemplate;


    /**
     * 채팅방 입장 , 실시간 메세지 읽음처리
     *
     * @param chatRoomId
     * @param accessor
     */
    @Transactional
    public void handleEnter(UUID chatRoomId, SimpMessageHeaderAccessor accessor ){
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ResourceNotFoundException("해당 채팅방을 찾지 못했습니다."));
        // 메세지 아이디 가져오기
        String requestEmail = (String) accessor.getSessionAttributes().get("username");
        List<Message> messages = messageRepository.findUnreadMessageIds(chatRoom, requestEmail);

        for(Message message : messages){
            message.markAsRead();
        }

        // UUID -> String 변환
        List<String> messageIdStrings = messages.stream()
                .map(message -> message.getId().toString())
                        .collect(Collectors.toList());


        messagingTemplate.convertAndSend(
                "/subscribe/chat/enter/" + chatRoomId,
                messageIdStrings);

    }


    /**
     *  TEXT 타입 메세지 저장 및 보내기
     *
     * @param request
     * @param chatRoomId
     * @param accessor
     *
     * @return ChatMessageResponseDto :: content, sendDate,
     */
    @Transactional
    public ChatMessageResponseDto sendMessage(ChatMessageRequestDto request, UUID chatRoomId, SimpMessageHeaderAccessor accessor){

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ResourceNotFoundException("해당 채팅방을 찾지 못했습니다."));

        String requestRole = (String) accessor.getSessionAttributes().get("role");
        String requestEmail = (String) accessor.getSessionAttributes().get("username");

        // 유저인지 디자이너인지 판별
        Message message = new Message();
        if(requestRole.equals("USER")){
            System.out.println("유저임");
            // 메세지 저장
            message = Message.saveMessage(request,requestEmail, SenderType.USER ,chatRoom);
        }else if(requestRole.equals("DESIGNER")){
            System.out.println("디자이너임");
            // 메세지 저장
            message = Message.saveMessage(request,requestEmail, SenderType.DESIGNER ,chatRoom);
        }

        messageRepository.save(message);
        // 상대방 온라인 여부 체크
        boolean isOnlinePartner = chattingOnlineService.isPartnerOnline(chatRoomId,requestEmail);

        // 상대방 온라인이면 메세지 바로 읽음
        if(isOnlinePartner){
            message.markAsRead();
        }

        // 마지막 메세지 , 보낸시간 업데이트
        chatRoom.updateLastMessage(request.content(),request.sendDate());

        return ChatMessageResponseDto.noFiles(message,requestEmail);

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
     * @param accessor
     * @return ChatMessageResponseDto :: content, sendDate, 리스트files
     */
    @Transactional
    public ChatMessageResponseDto sendFileMessage(ChatMessageRequestDto request, UUID chatRoomId, SimpMessageHeaderAccessor accessor){

        if (request.fileUrls() == null || request.fileUrls().isEmpty()) {
            throw new BindException("파일이 첨부되지 않았습니다.");
        }
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ResourceNotFoundException("해당 채팅방을 찾지 못했습니다."));

        String requestRole = (String) accessor.getSessionAttributes().get("role");
        String requestEmail = (String) accessor.getSessionAttributes().get("username");

        // 유저인지 디자이너인지 판별
        Message message = new Message();
        if(requestRole.equals("USER")){
            System.out.println("유저임");
            // 메세지 저장
            message = Message.saveFileMessage(request,requestEmail, SenderType.USER ,chatRoom, request.messageType());
        }else if(requestRole.equals("DESIGNER")){
            System.out.println("디자이너임");
            // 메세지 저장
            message = Message.saveFileMessage(request,requestEmail, SenderType.DESIGNER ,chatRoom, request.messageType());
        }

        message = messageRepository.save(message);

        // 파일 메세지들 저장
        final Message savedMessage = message;
        List<MessageFile> messageFiles = request.fileUrls().stream()
                .map(url -> MessageFile.save(url, savedMessage))
                .collect(Collectors.toList());
        messageFileRepository.saveAll(messageFiles);

        // 상대방 온라인 여부 체크
        boolean isOnlinePartner = chattingOnlineService.isPartnerOnline(chatRoomId,requestEmail);

        // 상대방 온라인이면 메세지 바로 읽음
        if(isOnlinePartner){
            message.markAsRead();
        }

        // 마지막 메세지 , 보낸 시각 업데이트
        chatRoom.updateLastMessage(request.content()+"(+파일)",request.sendDate());

        return ChatMessageResponseDto.withFileUrls(message,request.fileUrls(),requestEmail);
    }
}
