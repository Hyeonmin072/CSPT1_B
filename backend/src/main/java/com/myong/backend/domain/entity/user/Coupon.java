package com.myong.backend.domain.entity.user;


import com.myong.backend.domain.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "c_id")
    private UUID id; // 쿠폰 고유 키

    @Column(name = "c_name", nullable = false)
    private String name; // 이름

    @Column(name = "c_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType type; // 할인방식

    @Column(name = "c_price", nullable = false)
    private Integer price; // 할인값

    @Column(name = "c_get_date", nullable = false)
    private LocalDate getDate; // 유저가 수령가능한 날짜

    @Column(name = "c_use_date", nullable = false)
    private Integer useDate; // 유저가 수령 후 사용가능한 기간

    @Column(name = "c_expire_date", nullable = false)
    private LocalDate expireDate; // 삭제되기 전 살아있을 날짜 -> getDate + useDate의 일 수

    @Column(name = "c_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponStatus status = CouponStatus.UNUSED;  // 사용, 미사용 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 이 쿠폰을 등록한 가게

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<UserCoupon> userCoupons; // 받은 유저들

    @Builder
    public Coupon(String name, DiscountType type, Integer price, LocalDate getDate, Integer useDate, Shop shop) { // 고정금액 할인 쿠폰
        this.name = name;
        this.type = type;
        this.price = price;
        this.getDate = getDate;
        this.useDate = useDate;
        this.expireDate = getDate.plusDays(useDate);
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
