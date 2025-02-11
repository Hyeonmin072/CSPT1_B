package com.myong.backend.domain.entity.shop;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class ShopRegularHoliday {

    @Id
    @Column(name = "srh_id")
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop;

    @Column(name = "srh_day",nullable = false)
    private DayOfWeek day;

    public ShopRegularHoliday(Shop shop, DayOfWeek day) {
        this.shop = shop;
        this.day = day;
    }
}
