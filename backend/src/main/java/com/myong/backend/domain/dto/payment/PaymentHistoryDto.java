package com.myong.backend.domain.dto.payment;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class PaymentHistoryDto {
    private Long paymentHistoryId;

    @NotNull
    private Long price;

    @NotNull
    private String orderName;

    private boolean isPaySuccessYN;

    private LocalDateTime payDate;
}
