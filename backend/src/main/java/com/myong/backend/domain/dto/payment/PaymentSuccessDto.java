package com.myong.backend.domain.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentSuccessDto {
    String shopName; // 예약한 가게
    String menuName; // 예약한 메뉴명
    Long price; // 결제된 금액
    LocalDateTime date; // 결제 일시
}
