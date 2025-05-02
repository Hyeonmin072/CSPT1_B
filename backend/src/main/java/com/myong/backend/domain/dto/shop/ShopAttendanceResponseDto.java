package com.myong.backend.domain.dto.shop;

import com.myong.backend.domain.entity.designer.Status;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;

@Value
@Builder
public class ShopAttendanceResponseDto {

    LocalDate date; // 날짜

    String desingerName; // 디자이너 이름

    Status status; // 근태 상태

    LocalTime workTime; // 출근 시간

    LocalTime leaveTime; // 퇴근 시간

    String note; // 비고

}
