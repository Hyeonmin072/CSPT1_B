package com.myong.backend.domain.entity.user;

import lombok.Getter;

public enum Grade {
    NONE(0.0), BROZNE(0.01), SILVER(0.03), GOLD(0.05);

    @Getter
    private final Double discountPercent; // 할인율


    Grade(Double discountPercent) {
        this.discountPercent = discountPercent;
    }
}
