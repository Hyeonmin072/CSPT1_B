package com.myong.backend.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Coupon {

    @Id
    @Column(name = "c_id")
    private String id;

    @Column(name = "c_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponStatus status = CouponStatus.USE;

    @Column(name = "c_name", nullable = false)
    private String name;

    @Column(name = "c_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CouponType type;

    @Column(name = "c_amount", nullable = false)
    private Long amount;

    @Column(name = "c_expire", nullable = false)
    private LocalDateTime expire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    public Coupon(CouponStatus status, String name, CouponType type, Long amount, LocalDateTime expire, User user) {
        this.id = UUID.randomUUID().toString();
        this.status = status;
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.expire = expire;
        this.user = user;
    }
}
