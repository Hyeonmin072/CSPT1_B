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
    private String id;

    @Column(name = "cr_name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages;

    public ChatRoom(String id, String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
}
