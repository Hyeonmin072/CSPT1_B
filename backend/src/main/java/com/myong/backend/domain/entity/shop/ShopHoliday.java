package com.myong.backend.domain.entity.shop;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
public class ShopHoliday {

    @Id
    @Column(name = "sh_id",nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "sh_date",nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id")
    private Shop shop;
}
