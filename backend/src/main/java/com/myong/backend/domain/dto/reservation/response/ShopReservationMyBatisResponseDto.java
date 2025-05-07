package com.myong.backend.domain.dto.reservation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ShopReservationMyBatisResponseDto {

    LocalDateTime serviceDate; // 예약해놓은 날짜

    String userName; // 예약한 유저 이름

    String designerName; // 예약받은 디자이너 이름

    String menuName; // 예약한 메뉴 이름

    Integer menuPrice; // 예약한 메뉴 가격

    String reservationId; // 예약 아이디

    String userId; // 유저 아이디
}
