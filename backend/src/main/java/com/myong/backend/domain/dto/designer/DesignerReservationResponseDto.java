package com.myong.backend.domain.dto.designer;

import com.myong.backend.domain.entity.business.ReservationStatus;
import com.myong.backend.domain.entity.shop.Menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DesignerReservationResponseDto {

    private UUID reservationId;
    private String userName;
    private String menuName;
    private LocalDateTime serviceDate;
    private DayOfWeek dayOfWeek;
    private Integer menuPrice;
    private ReservationStatus reservationStatus;
}
