package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * 결제 호출 요청을 하는 DTO
 * 아래 필드들을 클라이언트 측에서 입력받는다
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    @NotNull
    private Long price; // 가격

    @NotNull
    private String reservMenuName; // 예약한 메뉴명

    @NotNull
    private UUID reservationId; // 예약 아이디

    private String yourSuccessUrl; // 성공 시 리다이렉트 할 URL

    private String yourFailUrl; // 실패 시 리다이렉트 할 URL

}
