package com.myong.backend.domain.dto.payment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentSuccessDto {
    String shopName; // 예약한 가게
    String shopEmail;
    LocalDateTime serviceDate;
    String designerImage;
    String designerName;
    String designrDesc;
    String menuName; // 예약한 메뉴명
    String menuDesc;
    String menuImage;
    Long price; // 결제된 금액
    LocalDateTime paymentDate; // 결제 일시
}
