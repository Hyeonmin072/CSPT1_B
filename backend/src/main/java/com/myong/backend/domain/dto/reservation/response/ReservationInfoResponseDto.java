package com.myong.backend.domain.dto.reservation.response;


import com.myong.backend.domain.entity.business.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReservationInfoResponseDto {

    private UUID reservationId;
    private LocalDateTime serviceDate;
    private String menu;
    private String shop;
    private String designer;
    private Integer price;
    private String menuImage;

    public static ReservationInfoResponseDto from (Reservation reservation) {
        return ReservationInfoResponseDto.builder()
                .reservationId(reservation.getId())
                .serviceDate(reservation.getServiceDate())
                .menu(reservation.getMenu().getName())
                .shop(reservation.getShop().getName())
                .designer(reservation.getDesigner().getName())
                .price(reservation.getPrice())
                .menuImage(reservation.getMenu().getImage())
                .build();
    }

}
