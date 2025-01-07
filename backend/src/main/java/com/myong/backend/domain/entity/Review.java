package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
public class Review {

    @Id
    @Column(name = "rv_id", nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "rv_rating")
    private Double rating;

    @Column(name = "rv_content")
    private String content;

    @Column(name = "rv_image")
    private String image;

    @Column(name = "rv_createdate")
    private LocalDateTime createdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "r_id")
    Reservation reservation;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "s_id")
//    Shop shop;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "d_id")
//    Desginer desginer;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "u_id")
//    User user;


}
