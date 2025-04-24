package com.myong.backend.controller;

import com.myong.backend.configuration.TossPaymentConfig;
import com.myong.backend.domain.dto.payment.PaymentFailDto;
import com.myong.backend.domain.dto.payment.PaymentHistoryDto;
import com.myong.backend.domain.dto.payment.PaymentSuccessDto;
import com.myong.backend.domain.dto.shop.PaymentRequestDto;
import com.myong.backend.domain.dto.shop.PaymentResponseDto;
import com.myong.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final TossPaymentConfig tossPaymentConfig;

/**
 * 결제 진행 흐름 (TossPayments)
 *
 * 1. 사용자가 최종 예약 페이지에서 [결제하기] 버튼 클릭
 *    -> 서버에서 결제 객체 생성 (orderId 포함)
 *    -> 결제에 필요한 정보를 클라이언트로 반환
 *
 * 2. 클라이언트에서 TossPayments.requestPayment() 호출
 *    -> 토스 결제창 띄움 (카드 정보 등 입력)
 *
 * 3. 사용자 입력 완료 후, 토스 서버가 successUrl 또는 failUrl로 리다이렉트
 *    -> 이 시점은 "결제 인증"만 완료 (돈 빠지지 않음)
 *
 * 4. 서버는 successUrl로 들어온 요청에서
 *    -> paymentKey, orderId, amount를 확인하고
 *    -> 서버에서 토스 결제 승인 API (POST /v1/payments/confirm) 호출
 *    -> 여기서 예외나 다른 오류 발생시, 결제 인증은 성공했지만 결제 승인까지 되지 못했으므로, 돈이 빠져나가지 않고 실패 처리됨. 즉, 사용자 결제는 실패로 끝난다
 *
 * 5. 결제 승인 성공 후
 *    -> 결제 상태 '성공'으로 업데이트 및 예약 테이블 생성 등 후속 처리 진행
 *
 * 6. 서버는 failUrl로 들어온 요청에서
 *    -> 결제 상태 '실패'로 업데이트
 *    -> 테스트 환경에서는 실제로 잔액이 부족하거나 유효기간이 만료된 카드 정보를 넣어도 결제 요청과 인증은 정상적으로 이뤄진다
 *    -> 가장 쉽게 failUrl 리다이렉트를 재현하는 방법은 모바일 환경에서 결제를 요청하고, 결제창을 닫는 방법이다
 *    -> 하지만 결제수단과 환경에 따라 failUrl로 이동하는 조건이 다르다는 점을 유의해야 한다
 *
 * 7. 사용자가 추후 결제를 취소하면
 *    -> 서버에서 토스 결제 취소 API (POST /v1/payments/{paymentKey}/cancel) 호출
 *    -> 결제 상태를 '취소'로 업데이트
 *    -> 관련 예약 데이터도 함께 삭제 (혹은 별도 상태로 변경)
 *    -> 이때, 결제 금액은 전액 환불되며, 취소 사유도 함께 저장됨
 *    -> 단, 토스 정책상 취소 가능 시간 및 조건은 PG 설정에 따라 달라질 수 있음 (예: 부분취소 여부, 영업일 기준 환불 처리 등)
 */


 // 클라이언트 a 요청 -> 결제 엔티티 관련 정보 클라이언트로 넘김
 // a가 성공한 경우, 클라이언트 b 요청 -> 결제 인증 API로 요청, 결제 인증 성공하거나 실패시, 토스 자체 서버에서 리다이렉트하여 우리 서버에서 결제 승인 API로 요청 및 기타 처리(결제 엔티티 상태 업데이트)
 // b가 성공한 경우, 클라리언트 c 요청 -> 예약 테이블 생성 로직,

 // b가 됐으나 c가 터지는 경우 등을 방지하기 위해 a 요청 시, 결제 객체와 함께 예약에 필요한 정보도 미리 저장 (예약 임시 테이블 또는 결제 객체에 필드로 포함) /payment/success에서 결제 승인 후, 이 정보 기반으로 예약 생성
 // 이후 b가 되는 결제 승인이나 실패 엔드포인트에서 임싣 데이터를 가지고 예약을 생성하거나, 임시 데이터를 날림
 // 예약 임시 데이터는 redis를 이용하기
    
    /**
     * 결제 객체 생성 및 예약 임시 데이터 생성
     */
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDto> requestTossPayment(@Validated @RequestBody PaymentRequestDto request) {
        return ResponseEntity.ok(paymentService.requestTossPayment(request));
    }

    /**
     * 결제 인증 성공 시
     */
    @GetMapping("/payment/success")
    public ResponseEntity<PaymentSuccessDto> tossPaymentSuccess(@RequestParam String paymentKey,
                                                                @RequestParam("orderId") String paymentId,
                                                                @RequestParam Long amount) {
        return ResponseEntity.ok().body(paymentService.tossPaymentSuccess(paymentKey, paymentId, amount));
    }

    /**
     * 결제 인증 실패 시
     */
    @GetMapping("/payment/fail")
    public ResponseEntity<PaymentFailDto> tossPaymentFail(@RequestParam String code,
                                                          @RequestParam("orderId") String paymentId,
                                                          @RequestParam String message) {
        return ResponseEntity.ok().body(paymentService.tossPaymentFail(code, paymentId, message));
    }

    /**
     * 결제 승인 완료 후, 취소 시
     */
    @PostMapping("/payment/cancel/point")
    public ResponseEntity<Map> tossPaymentCancel(@AuthenticationPrincipal User principal,
                                                 @RequestParam String paymentKey,
                                                 @RequestParam String cancelReason) {
        return ResponseEntity.ok().body(paymentService.tossPaymentCancel(principal.getUsername(), paymentKey, cancelReason));
    }

    /**
     * 결제 내역 조회
     */
    @GetMapping("/payment/history")
    public ResponseEntity<List<PaymentHistoryDto>> findAllChargingHistories() {
        return ResponseEntity.ok().body(paymentService.findAllChargingHistories());
    }
}
