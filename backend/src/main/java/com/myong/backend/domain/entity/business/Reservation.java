package com.myong.backend.domain.entity.business;


import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Menu;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {

    @Id
    @Column(name = "r_id")
    private UUID id = UUID.randomUUID(); // 예약 고유 키

    @Column(name = "r_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.WAIT; // 예약 상태

    @Column(name = "r_create_date", nullable = false)
    private LocalDateTime createDate; // 예약을 접수한 날짜

    @Column(name = "r_service_date", nullable = false)
    private LocalDateTime serviceDate; // 서비스를 받을 날짜

    @Column(name = "r_pay_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod payMethod; // 결제 수단

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "m_id", nullable = false)
    private Menu menu; // 메뉴 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id")
    private Coupon coupon; // 쿠폰 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id",nullable = false)
    private Shop shop; // 가게 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id",nullable = false)
    private Designer designer; // 디자이너 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id",nullable = false)
    private User user; // 유저 고유 키

    public Reservation(LocalDateTime createDate, LocalDateTime serviceDate, Menu menu, Shop shop, Designer designer, User user) {
        this.createDate = createDate;
        this.serviceDate = serviceDate;
        this.menu = menu;
        this.shop = shop;
        this.designer = designer;
        this.user = user;
    }


}
