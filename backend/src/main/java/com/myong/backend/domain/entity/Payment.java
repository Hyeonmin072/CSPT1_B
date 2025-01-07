package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
public class Payment {
    @Id
    @Column(name = "p_id", nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "p_url")
    private String url;

    @Column(name = "p_amount")
    private Long amount;

    @Column(name = "p_status")
    @Enumerated(EnumType.STRING)
    PayStatus status = PayStatus.INCOMPLETE;

    @Column(name = "p_paydate")
    private LocalDateTime paydate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "r_id")
    Reservation reservation;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "s_id")
//    Shop shop


    public Payment(){}




}
