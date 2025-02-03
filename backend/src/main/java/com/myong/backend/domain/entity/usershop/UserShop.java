package com.myong.backend.domain.entity.usershop;

import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class UserShop {

    @EmbeddedId
    private UserShopId id; // 유저 가게 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    public UserShop(User user, Shop shop) {
        this.id = new UserShopId(user.getId(), shop.getId());
        this.user = user;
        this.shop = shop;
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    /**
     *  복합키(직렬화 사용)
     */
    private static class UserShopId implements Serializable {
        private String userId;
        private String shopId;
    }

}
