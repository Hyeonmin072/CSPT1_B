package com.myong.backend.domain.dto.shop;

import com.myong.backend.domain.entity.designer.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class ShopAttendanceResponseDto {

    private LocalDate date; // 날짜

    private String desingerName; // 디자이너 이름

    private Status status; // 근태 상태

    private LocalTime workTime; // 출근 시간

    private LocalTime leaveTime; // 퇴근 시간

    private String note; // 비고

}
