package com.myong.backend.domain.entity.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Coupon {

    @Id
    @Column(name = "c_id")
    private String id; // 쿠폰 고유 키

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

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<UserCoupon> userCoupons; // 받은 유저들

    public Coupon(String name, Long fixedAmount, LocalDateTime expireDate) { // 고정금액 할인 쿠폰
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = CouponType.FIXED;
        this.fixedAmount = fixedAmount;
        this.expireDate = expireDate;
    }

    public Coupon(String name, Double percentAmount, LocalDateTime expireDate) { // 퍼센트 할인 쿠폰
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = CouponType.PERCENT;
        this.percentAmount = percentAmount;
        this.expireDate = expireDate;
    }
}
