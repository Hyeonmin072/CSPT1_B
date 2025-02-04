package com.myong.backend.domain.entity.shop;


import com.myong.backend.domain.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
public class ShopRegularHoliday {

    @Id
    @Column(name = "srh_id",nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "srh_day",nullable = false)
    private String day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id")
    private Shop shop;

}
