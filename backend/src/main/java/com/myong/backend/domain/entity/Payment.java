package com.myong.backend.domain.entity;

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
    private String id;

    @Column(name = "p_url", nullable = false)
    private String url;

    @Column(name = "p_amount", nullable = false)
    private Long amount;

    @Column(name = "p_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayStatus status = PayStatus.INCOMPLETE;

    @Column(name = "p_paydate")
    private LocalDateTime paydate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "r_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop;

    public Payment(String url, Long amount, PayStatus status, Reservation reservation, Shop shop) {
        this.id = UUID.randomUUID().toString();
        this.url = url;
        this.amount = amount;
        this.status = status;
        this.reservation = reservation;
        this.shop = shop;
    }
}
