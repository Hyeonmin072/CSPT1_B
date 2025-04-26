package com.myong.backend.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempReservationData implements Serializable {
    private LocalDateTime serviceDate;
    private String designerEmail;
    private String shopEmail;
    private String menuId;
    private String couponId;
    private int price;
}
