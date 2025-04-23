package com.myong.backend.controller;

import com.myong.backend.configuration.TossPaymentConfig;
import com.myong.backend.domain.dto.payment.PaymentHistoryDto;
import com.myong.backend.domain.dto.payment.PaymentFailDto;
import com.myong.backend.domain.dto.payment.PaymentSuccessDto;
import com.myong.backend.domain.dto.shop.PaymentRequestDto;
import com.myong.backend.domain.dto.shop.PaymentResponseDto;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
     * 결제 테이블 생성 <- 예약 테이블 생성 전 결제 진행위해 필요
     * @param request
     * @return
     */
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDto> requestTossPayment(@RequestBody @Valid PaymentRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal = (UserDetailsDto) authentication.getPrincipal();
        PaymentResponseDto response = paymentService.requestTossPayment(request, principal.getUsername());
        response.setSuccessUrl(request.getYourSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl() : request.getYourSuccessUrl());
        response.setFailUrl(request.getYourFailUrl() == null ? tossPaymentConfig.getFailUrl() : request.getYourFailUrl());
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 성공 시
     * @param paymentKey
     * @param reservationId
     * @param amount
     * @return
     */
    @GetMapping("/payment/success")
    public ResponseEntity<PaymentSuccessDto> tossPaymentSuccess(@RequestParam String paymentKey,
                                                                @RequestParam("orderId") String reservationId,
                                                                @RequestParam Long amount) {
        return ResponseEntity.ok().body(paymentService.tossPaymentSuccess(paymentKey, reservationId, amount));
    }

    /**
     * 결제 실패 시
     * @param code
     * @param reservationId
     * @param message
     * @return
     */
    @GetMapping("/payment/fail")
    public ResponseEntity<PaymentFailDto> tossPaymentFail(@RequestParam String code,
                                                          @RequestParam("orderId") String reservationId,
                                                          @RequestParam String message) {
        paymentService.tossPaymentFail(code, reservationId, message);
        return ResponseEntity.ok().body(
                PaymentFailDto.builder()
                .errorCode(code)
                .reservationId(reservationId)
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
