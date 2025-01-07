package com.myong.backend.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
public class Coupon {

    @Id
    @Column(name = "c_id", nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "c_status")
    @Enumerated(EnumType.STRING)
    CouponStatus status = CouponStatus.USE;

    @Column(name = "c_name")
    private String name;

    @Column(name = "c_type")
    @Enumerated(EnumType.ORDINAL)
    private CouponType type;

    @Column(name = "c_amount")
    private Long amount;

    @Column(name = "c_expire")
    private LocalDateTime expire;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "u_id")
//    User user;

    public Coupon(){}
}
