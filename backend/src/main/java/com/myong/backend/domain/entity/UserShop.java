package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class UserShop {

    @Id
    private String id; // 유저 가게 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    public UserShop(User user, Shop shop) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.shop = shop;
    }
}
