package com.myong.backend.domain.entity.usershop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @Column(name = "cr_id")
    private UUID id = UUID.randomUUID(); // 채팅방 고유 키

    @Column(name = "cr_name", nullable = false)
    private String name; // 채팅방 이름

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages; // 채팅방의 메시지들

    public ChatRoom(String name) {
        this.name = name;
    }
}
