package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Review {

    @Id
    @Column(name = "rv_id", nullable = false)
    private String id;

    @Column(name = "rv_rating", nullable = false)
    private Double rating;

    @Column(name = "rv_content", nullable = false)
    private String content;

    @Column(name = "rv_image")
    private String image;

    @Column(name = "rv_createdate", nullable = false)
    private LocalDateTime createdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "r_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer desginer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    public Review(String content, Double rating, String id, LocalDateTime createdate, Reservation reservation, Shop shop, Designer desginer, User user) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.rating = rating;
        this.id = id;
        this.createdate = createdate;
        this.reservation = reservation;
        this.shop = shop;
        this.desginer = desginer;
        this.user = user;
    }
}
