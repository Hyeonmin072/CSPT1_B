package com.myong.backend.controller;

import com.myong.backend.configuration.TossPaymentConfig;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final TossPaymentConfig tossPaymentConfig;

    /**
     * 결제 테이블 생성
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
     * 결제 성공 시 처리
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
     * 결제 실패 시 처리
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
}
