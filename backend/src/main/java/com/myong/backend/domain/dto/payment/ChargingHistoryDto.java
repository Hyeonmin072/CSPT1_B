package com.myong.backend.domain.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargingHistoryDto {

    @NotNull
    private Long price;

    @NotNull
    private String reservationName;

    @NotNull
    private Boolean isPaySuccessYN;

    private LocalDateTime createDate;
}
