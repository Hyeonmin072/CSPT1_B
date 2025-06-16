package com.myong.backend.domain.entity.business;


import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Menu;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "r_id")
    private UUID id; // 예약 고유 키

    @CreatedDate
    @Column(name = "r_create_date", updatable = false)
    private LocalDateTime createDate; // 예약을 접수한 날짜

    @Column(name = "r_service_date", nullable = false)
    private LocalDateTime serviceDate; // 서비스를 받을 날짜

    @Column(name = "r_price")
    private Integer price; // 결제 금액

    @Column(name = "r_status")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.INCOMPLETE;   // 서비스 완료, 미완료 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "m_id")
    private Menu menu; // 메뉴 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id")
    private Coupon coupon; // 쿠폰 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer; // 디자이너 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    public Reservation(LocalDateTime serviceDate, Integer price, Menu menu, Shop shop, Designer designer, User user, Coupon coupon) {
        this.serviceDate = serviceDate;
        this.price = price;
        this.menu = menu;
        this.shop = shop;
        this.designer = designer;
        this.user = user;
        this.coupon = coupon;
    }

    public Reservation(LocalDateTime serviceDate, Integer price,Menu menu, Shop shop, Designer designer, User user) {
        this.serviceDate = serviceDate;
        this.price = price;
        this.menu = menu;
        this.shop = shop;
        this.designer = designer;
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Reservation that = (Reservation) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void deleteMenu() {
        this.menu = null;
    }
}
