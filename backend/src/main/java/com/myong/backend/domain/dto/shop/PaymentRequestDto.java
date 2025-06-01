package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 결제 호출 요청을 하는 DTO
 * 아래 필드들을 클라이언트 측에서 입력받는다
 */
@Getter
@NoArgsConstructor
public class PaymentRequestDto {
    @NotNull
    private Long price; // 가격

    /**
     * 아래는 임시 예약 객체 생성용
     */
    @NotNull
    private LocalDateTime serviceDate; // 서비스 받을 날짜

    @NotNull
    private String designerEmail; // 디자이너 이메일

    @NotNull
    private String shopEmail; //가게 이메일

    private String couponId; // 쿠폰 아이디

    @NotBlank
    private String menuId; // 메뉴 아이디

}
