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
    private String id; // 쿠폰 고유 키

    @Column(name = "c_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponStatus status; // 상태

    @Column(name = "c_name", nullable = false)
    private String name; // 이름

    @Column(name = "c_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType type; // 할인방식

    @Column(name = "c_fixed_amount")
    private Long fixedAmount; // 할인 고정금액

    @Column(name = "c_percent_amount")
    private Double percentAmount; // 할인 퍼센트

    @Column(name = "c_expire_date", nullable = false)
    private LocalDateTime expireDate; // 사용 기한

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    public Coupon(String name, Long fixedAmount, LocalDateTime expireDate, User user) {
        this.id = UUID.randomUUID().toString();
        this.status = CouponStatus.NOTUSE;
        this.name = name;
        this.type = CouponType.FIXED;
        this.fixedAmount = fixedAmount;
        this.expireDate = expireDate;
        this.user = user;
    }

    public Coupon(String name, Double percentAmount, LocalDateTime expireDate, User user) {
        this.id = UUID.randomUUID().toString();
        this.status = CouponStatus.NOTUSE;
        this.name = name;
        this.type = CouponType.PERCENT;
        this.percentAmount = percentAmount;
        this.expireDate = expireDate;
        this.user = user;
    }
}
