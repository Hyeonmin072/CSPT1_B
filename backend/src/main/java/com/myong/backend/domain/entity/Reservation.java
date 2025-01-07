package com.myong.backend.domain.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Reservation {

    @Id
    @Column(name = "r_id", nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "r_status")
    @Enumerated(EnumType.STRING)
    ReservationStatus status = ReservationStatus.RECEIPT;

    @Column(name = "r_createdate")
    private LocalDateTime createDate;

    @Column(name = "r_reservdate")
    private LocalDateTime reservDate;

    @Column(name = "r_payment")
    @Enumerated(EnumType.STRING)
    ReservationPayment payment;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "m_id")
//    Menu menu;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "c_id")
//    Coupon coupon

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "s_id")
//    Shop shop

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "d_id")
//    Designer designer;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "u_id")
//    User user;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    List<Payment> paymentList = new ArrayList<>();

    public Reservation(){

    }




}
