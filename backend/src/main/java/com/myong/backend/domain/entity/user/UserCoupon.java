package com.myong.backend.domain.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserCoupon {

    @EmbeddedId
    private UserCouponId id; // 유저 쿠폰 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id", nullable = false)
    private Coupon coupon; // 쿠폰 고유 키

    @CreatedDate
    @Column(name = "uc_create_date", updatable = false)
    private LocalDate createDate; // 생성일 (유저가 쿠폰을 받은 날짜)

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserCoupon that = (UserCoupon) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    /**
     *  복합키(직렬화 사용)
     */
    private static class UserCouponId implements Serializable {
        private UUID userId;
        private UUID couponId;
    }


    public UserCoupon(User user, Coupon coupon) {
        this.id = new UserCouponId(user.getId(), coupon.getId());
        this.user = user;
        this.coupon = coupon;
    }
}
