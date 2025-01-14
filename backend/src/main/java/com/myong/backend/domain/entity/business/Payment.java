package com.myong.backend.domain.entity.business;

import com.myong.backend.domain.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @Column(name = "p_id", nullable = false)
    private String id; // 결제 고유 키

    @Column(name = "p_url", nullable = false)
    private String url; // 결제 URL

    @Column(name = "p_amount", nullable = false)
    private Long amount; // 결제 금액

    @Column(name = "p_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayStatus status; // 결제 상태

    @Column(name = "p_pay_date")
    private LocalDateTime payDate; // 결제완료 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "r_id", nullable = false)
    private Reservation reservation; // 예약 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    public Payment(String url, Long amount, Reservation reservation, Shop shop) {
        this.id = UUID.randomUUID().toString();
        this.url = url;
        this.amount = amount;
        this.status = PayStatus.INCOMPLETE;
        this.reservation = reservation;
        this.shop = shop;
    }
}
