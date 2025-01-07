package com.myong.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
public class ChatRoom {

    @Id
    @Column(name = "cr_id",nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "cr_name")
    private String name;
}
