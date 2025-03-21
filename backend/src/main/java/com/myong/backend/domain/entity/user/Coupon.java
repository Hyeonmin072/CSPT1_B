package com.myong.backend.domain.entity.user;


import com.myong.backend.domain.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Coupon {

    @Id
    @Column(name = "c_id")
    private UUID id = UUID.randomUUID(); // 쿠폰 고유 키

    @Column(name = "c_name", nullable = false)
    private String name; // 이름

    @Column(name = "c_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType type; // 할인방식

    @Column(name = "c_amount", nullable = false)
    private Long amount; // 할인값

    @Column(name = "c_get_date", nullable = false)
    private Period getDate; // 수령가능한 기간

    @Column(name = "c_use_date", nullable = false)
    private Period useDate; // 수령 후 사용 가능 기간

    @CreatedDate
    @Column(name = "c_create_date", updatable = false)
    private LocalDate createDate; // 생성일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 이 쿠폰을 등록한 가게

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<UserCoupon> userCoupons; // 받은 유저들

    @Builder
    public Coupon(String name, DiscountType type, Long amount, Period getDate, Period useDate, Shop shop) { // 고정금액 할인 쿠폰
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.getDate =  getDate;
        this.useDate = useDate;
        this.shop = shop;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Coupon coupon = (Coupon) o;
        return getId() != null && Objects.equals(getId(), coupon.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
