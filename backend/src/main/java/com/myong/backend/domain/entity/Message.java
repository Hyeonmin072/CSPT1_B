package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Message {

    @Id
    @Column(name = "me_id")
    private String id; // 메시지 고유 키

    @Column(name = "me_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType; // 타입

    @Column(name = "me_content", nullable = false)
    private String content; // 텍스트 내용, 메시지 타입에 따라 일반 텍스트인지, 클라우드URL인지 구분되다

    @Column(name = "me_create_date", nullable = false)
    private LocalDateTime createDate; // 전송 시간

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cr_id", nullable = false)
    private ChatRoom chatRoom; // 채팅방 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    public Message(MessageType type, String content, LocalDateTime createDate, ChatRoom chatRoom, Shop shop, User user) {
        this.id = UUID.randomUUID().toString();
        this.messageType = type;
        this.content = content;
        this.createDate = createDate;
        this.chatRoom = chatRoom;
        this.shop = shop;
        this.user = user;
    }
}
