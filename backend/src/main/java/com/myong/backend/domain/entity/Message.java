package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
public class Message {

    @Id
    @Column(name = "me_id")
    private String id = UUID.randomUUID().toString();

    @Column(name = "me_type")
    @Enumerated(EnumType.STRING)
    MessageType messageType;

    @Column(name = "me_context")
    private String context;

    @Column(name = "me_image")
    private String image;

    @Column(name = "me_file")
    private String file;

    @Column(name = "me_createdate")
    private LocalDateTime createdate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cr_id")
    ChatRoom chatRoom;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "s_id")
//    Shop shop;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "u_id")
//    User user;

    public Message(){}
}
