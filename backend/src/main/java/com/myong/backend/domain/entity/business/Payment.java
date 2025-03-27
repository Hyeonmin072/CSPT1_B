package com.myong.backend.domain.entity.business;

import com.myong.backend.domain.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "p_id")
    private UUID id; // 결제 고유 키

    @Column(name = "p_url", nullable = false)
    private String url; // 결제 URL

    @Column(name = "p_price", nullable = false)
    private Integer price; // 결제 금액

    @Column(name = "p_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayStatus status = PayStatus.INCOMPLETE; // 결제 상태

    @Column(name = "p_pay_date")
    private LocalDateTime payDate; // 결제완료 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "r_id", nullable = false)
    private Reservation reservation; // 예약 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    public Payment(String url, Integer price, Reservation reservation, Shop shop) {
        this.url = url;
        this.price = price;
        this.reservation = reservation;
        this.shop = shop;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Payment payment = (Payment) o;
        return getId() != null && Objects.equals(getId(), payment.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
