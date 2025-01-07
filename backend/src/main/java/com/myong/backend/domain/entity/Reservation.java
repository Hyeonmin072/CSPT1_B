package com.myong.backend.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {

    @Id
    @Column(name = "r_id", nullable = false)
    private String id;

    @Column(name = "r_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.RECEIPT;

    @Column(name = "r_createdate", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "r_reservdate", nullable = false)
    private LocalDateTime reservDate;

    @Column(name = "r_paymethod")
    @Enumerated(EnumType.STRING)
    private PaymentMethod payMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "m_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id",nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id",nullable = false)
    private Designer designer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id",nullable = false)
    private User user;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    public Reservation(ReservationStatus status, LocalDateTime createDate, LocalDateTime reservDate, Menu menu, Shop shop, Designer designer, User user) {
        this.id = UUID.randomUUID().toString();
        this.status = status;
        this.createDate = createDate;
        this.reservDate = reservDate;
        this.menu = menu;
        this.shop = shop;
        this.designer = designer;
        this.user = user;
    }


}
