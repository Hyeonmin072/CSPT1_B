package com.myong.backend.domain.dto.payment;

import lombok.Data;

@Data
public class PaymentApprovalDto {
    String company; // 회사명
    String number; // 카드번호
    String installmentPlanMonths; // 할부 개월
    String isInterestFree;
    String approveNo; // 승인번호
    String userCardPoint; // 카드 포인트 사용 여부
    String cardType; // 카드 타입
    String ownerType; // 소유자 타입
    String acquireStatus; // 승인 상태
    String reciptUrl; // 영수증 URL
}
