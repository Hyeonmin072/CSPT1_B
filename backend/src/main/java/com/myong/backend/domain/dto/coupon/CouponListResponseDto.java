package com.myong.backend.domain.dto.coupon;

import com.myong.backend.domain.entity.user.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Period;

@Getter
@AllArgsConstructor
public class CouponListResponseDto {
    private String name;
    private DiscountType type;
    private Long amount;
    private Period getDate;
    private Period useDate;
}
