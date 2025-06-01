package com.myong.backend.domain.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentFailDto {
    String errorCode;
    String errorMessage;
    String paymentId;
}
