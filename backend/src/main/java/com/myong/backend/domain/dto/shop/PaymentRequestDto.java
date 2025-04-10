package com.myong.backend.domain.dto.shop;

import com.myong.backend.domain.entity.business.Payment;
import com.myong.backend.domain.entity.business.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * 결제 호출 요청을 하는 DTO
 * 아래 필드들을 프론트에서 입력받음
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    @NotNull
    private PaymentMethod paymentMethod; // 결제 타입

    @NotNull
    private Integer price; // 가격

    @NotNull
    private String reservMenuName; // 예약한 메뉴명

    private String successUrl; // 성공 시 리다이렉트 할 URL

    private String failUrl; // 실패 시 리다이렉트 할 URL

    public Payment toEntity() {
        return Payment.builder()
                .paymentMethod(paymentMethod)
                .price(price)
                .reservMenuName(reservMenuName)
                .id(UUID.randomUUID())
                .paySuccessYN(false)
                .build();
    }

}
