package com.myong.backend.domain.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentHistoryDto {

    @NotNull
    private Long price;

    @NotNull
    private String reservationName;

    @NotNull
    private Boolean isPaySuccessYN;

    private LocalDateTime createDate;
}
