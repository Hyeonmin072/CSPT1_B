package com.myong.backend.domain.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class UserCoupon {

    @EmbeddedId
    private UserCouponId id; // 유저 쿠폰 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id", nullable = false)
    private Coupon coupon; // 쿠폰 고유 키


    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    /**
     *  복합키(직렬화 사용)
     */
    private static class UserCouponId implements Serializable {
        private String userId;
        private String couponId;
    }


    public UserCoupon(User user, Coupon coupon) {
        this.id = new UserCouponId(user.getId(), coupon.getId());
        this.user = user;
        this.coupon = coupon;
    }
}
