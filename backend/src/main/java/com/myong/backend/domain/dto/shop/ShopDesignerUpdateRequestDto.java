package com.myong.backend.domain.dto.shop;

import com.myong.backend.domain.entity.designer.RegularHoliday;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ShopDesignerUpdateRequestDto {
    @NotNull
    LocalTime workTime; // 디자이너 정시 출근 시간

    @NotNull
    LocalTime leaveTime; // 디자이너 정시 퇴근 시간

    @NotNull
    RegularHoliday regularHoliday; // 디자이너 정기 휴무일
}
