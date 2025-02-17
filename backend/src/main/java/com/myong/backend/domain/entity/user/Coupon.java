package com.myong.backend.domain.entity.user;


import com.myong.backend.domain.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Period;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 이 쿠폰을 등록한 가게

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<UserCoupon> userCoupons; // 받은 유저들

    public Coupon(String name, DiscountType type, Long amount, Period getDate, Period useDate, Shop shop) { // 고정금액 할인 쿠폰
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.getDate =  getDate;
        this.useDate = useDate;
        this.shop = shop;
    }
}
