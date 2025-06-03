package com.myong.backend.domain.dto.shop;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 응답으로 올 DTO
 * PaymentRequestDto로 받은 정보들을 검증 -> 실제 토스페이먼츠에서 결제 요청을
 * 하기 위해 필요한 값들을 포함해 PaymentResponseDto로 반환
 */
@Getter
@Builder
public class PaymentResponseDto {

    private Long price; // 가격

    private String reservMenuName; // 예약한 메뉴명

    private UUID paymentId; // 결제 아이디

    private String userEmail; // 유저 이메일

    private String userName; // 유저 이름

    private String successUrl; // 성공 시 리다이렉트 할 URL

    private String failUrl; // 실패 시 리다이렉트 할 URL

    private String failReason; // 실패 이유

    private boolean cancelYN; // 취소 YN

    private String cancelReason; // 취소 이유

    private LocalDateTime createDate; // 결제 시간


}
