package com.myong.backend.domain.dto.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
public class ShopDesignerUpdateRequestDto {

    @NotBlank
    @Email
    String shopEmail; // 사업자 이메일

    @NotBlank
    @Email
    String designerEmail; // 디자이너 이메일


    @NotNull
    LocalTime workTime; // 디자이너 정시 출근 시간

    @NotNull
    LocalTime leaveTime; // 디자이너 정시 퇴근 시간

    @NotNull
    DayOfWeek regularHoliday; // 디자이너 정기 휴무일
}
