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
    private String id;

    @Column(name = "me_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(name = "me_context")
    private String context;

    @Column(name = "me_image")
    private String image;

    @Column(name = "me_file")
    private String file;

    @Column(name = "me_createdate", nullable = false)
    private LocalDateTime createDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cr_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    public Message(MessageType messageType, LocalDateTime createDate, ChatRoom chatRoom, Shop shop, User user) {
        this.id = UUID.randomUUID().toString();
        this.messageType = messageType;
        this.createDate = createDate;
        this.chatRoom = chatRoom;
        this.shop = shop;
        this.user = user;
    }
}
