package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopAttendanceRequestDto {

    @NotBlank
    private String shopEmail; // 가게 이메일

    @NotNull
    private Integer year; // 연

    @NotNull
    private Integer month; // 월

    @NotNull
    private Integer day; // 일

    @NotBlank
    private String designerName; // 디자이너 이름

    private OrderBy order; // 정렬

    public enum OrderBy {
        DATE, DESIGNER_NAME, STATUS, WORK, LEAVE // 날짜순, 디자이너이름순, 출근시간순, 퇴근시간순
    }
}
