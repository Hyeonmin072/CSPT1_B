package com.myong.backend.controller;

import com.myong.backend.configuration.TossPaymentConfig;
import com.myong.backend.domain.dto.shop.PaymentRequestDto;
import com.myong.backend.domain.dto.shop.PaymentResponseDto;
import com.myong.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final TossPaymentConfig tossPaymentConfig;

    @PostMapping("/toss")
    public ResponseEntity<PaymentResponseDto> requestTossPayment(@AuthenticationPrincipal User principal, @RequestBody @Valid PaymentRequestDto request) {
        PaymentResponseDto response = paymentService.requestTossPayment(request.toEntity(), principal.getUsername()).toPaymentResponseDto();
        response.setSuccessUrl(request.getYourSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl() : request.getYourSuccessUrl());
        response.setFailUrl(request.getYourFailUrl() == null ? tossPaymentConfig.getFailUrl() : request.getYourFailUrl());
        return ResponseEntity.ok(response);
    }
}
