package com.myong.backend.domain.dto.designer;

import com.myong.backend.domain.entity.shop.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DesignerReservationResponseDto {

    String userName;//사용자 이름

    Menu menu;//메뉴

    LocalDateTime serviceDate;//예약일짜
    
    DayOfWeek dayOfWeek;//요일
}
