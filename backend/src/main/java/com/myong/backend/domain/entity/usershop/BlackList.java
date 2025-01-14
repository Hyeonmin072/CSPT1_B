package com.myong.backend.domain.entity.usershop;

import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class BlackList {

    @Id
    @Column(name = "b_id")
    private String id; // 고유 키

    @Column(nullable = false, updatable = false, name = "b_reason")
    private String reason; // 차단 사유

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    public BlackList(String reason,User user, Shop shop) {
        this.id = UUID.randomUUID().toString();
        this.reason = reason;
        this.user = user;
        this.shop = shop;
    }


}
