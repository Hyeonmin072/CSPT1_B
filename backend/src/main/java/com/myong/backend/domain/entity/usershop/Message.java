package com.myong.backend.domain.entity.usershop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Message {

    @Id
    @Column(name = "me_id")
    private UUID id = UUID.randomUUID(); // 메시지 고유 키

    @Column(name = "me_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType; // 타입

    @Column(name = "me_content", nullable = false)
    private String content; // 내용, 메시지 타입에 따라 일반 텍스트인지, 클라우드URL인지 구분되다

    @CreatedDate
    @Column(name = "me_send_date")
    private LocalDateTime sendDate; // 전송 시간
    
    @Column(name = "me_sender", nullable = false)
    private UUID sender; // 보낸 사람 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cr_id", nullable = false)
    private ChatRoom chatRoom; // 채팅방 고유 키

    public Message(MessageType messageType, String content, UUID sender, ChatRoom chatRoom) {
        this.messageType = messageType;
        this.content = content;
        this.sender = sender;
        this.chatRoom = chatRoom;
    }




}
