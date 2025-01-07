package com.myong.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @Column(name = "cr_id")
    private String id;

    @Column(name = "cr_name")
    private String name;

    public ChatRoom(String id) {
        this.id = UUID.randomUUID().toString();
    }
}
