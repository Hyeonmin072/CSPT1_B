package com.myong.backend.domain.dto.coupon;

import lombok.Getter;

@Getter
public class CouponRegisterRequestDto {
    private String name;
    private Integer getDate;
    private Integer useDate;
    private String type;
    private Long amount;
    private String shopEmail;
}
