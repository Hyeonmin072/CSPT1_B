package com.myong.backend.domain.dto.reservation.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ShopReservationResponseDto {

    @NotNull
    LocalDateTime serviceDate; // 예약해놓은 날짜

    @NotBlank
    String userName; // 예약한 유저 이름

    @NotBlank
    String designerName; // 예약받은 디자이너 이름

    @NotBlank
    String menuName; // 예약한 메뉴 이름

    @NotNull
    Integer menuPrice; // 예약한 메뉴 가격

    @NotBlank
    String reservationId; // 예약 아이디
}
