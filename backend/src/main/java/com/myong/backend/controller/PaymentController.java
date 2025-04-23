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
     * 결제 객체 생성 <- 예약 객체 생성 후 바로 진행, 실제 결제진행 전 필요
     */
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDto> requestTossPayment(@Validated @RequestBody PaymentRequestDto request) {
        return ResponseEntity.ok(paymentService.requestTossPayment(request));
    }

    /**
     * 결제 성공 시
     * @param paymentKey
     * @param paymentId
     * @param amount
     * @return
     */
    @GetMapping("/payment/success")
    public ResponseEntity<PaymentSuccessDto> tossPaymentSuccess(@RequestParam String paymentKey,
                                                                @RequestParam("orderId") String paymentId,
                                                                @RequestParam Long amount) {
        return ResponseEntity.ok().body(paymentService.tossPaymentSuccess(paymentKey, paymentId, amount));
    }

    /**
     * 결제 실패 시
     * @param code
     * @param paymentId
     * @param message
     * @return
     */
    @GetMapping("/payment/fail")
    public ResponseEntity<PaymentFailDto> tossPaymentFail(@RequestParam String code,
                                                          @RequestParam("orderId") String paymentId,
                                                          @RequestParam String message) {
        paymentService.tossPaymentFail(code, paymentId, message);
        return ResponseEntity.ok().body(
                PaymentFailDto.builder()
                .errorCode(code)
                .reservationId(paymentId)
                .errorMessage(message)
                .build()
        );
    }

    /**
     * 결제 취소 시
     * @param principal
     * @param paymentKey
     * @param cancelReason
     * @return
     */
    @PostMapping("/payment/cancel/point")
    public ResponseEntity<Map> tossPaymentCancel(@AuthenticationPrincipal User principal,
                                                 @RequestParam String paymentKey,
                                                 @RequestParam String cancelReason) {
        return ResponseEntity.ok().body(paymentService.tossPaymentCancel(principal.getUsername(), paymentKey, cancelReason));
    }


    /**
     * 결제 내역 조회
     * @return
     */
    @GetMapping("/payment/history")
    public ResponseEntity<List<PaymentHistoryDto>> findAllChargingHistories() {
        return ResponseEntity.ok().body(paymentService.findAllChargingHistories());
    }
}
