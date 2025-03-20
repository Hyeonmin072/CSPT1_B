package com.myong.backend.domain.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShopReservationResponseDto {

    @NotNull
    private LocalDateTime serviceDate; // 예약해놓은 날짜

    @NotBlank
    private String userName; // 예약한 유저 이름

    @NotBlank
    private String designerName; // 예약받은 디자이너 이름

    @NotBlank
    private String menuName; // 예약한 메뉴 이름

    @NotNull
    private Integer menuPrice; // 예약한 메뉴 가격

    @NotBlank
    private String reservationId; // 예약 아이디
}
