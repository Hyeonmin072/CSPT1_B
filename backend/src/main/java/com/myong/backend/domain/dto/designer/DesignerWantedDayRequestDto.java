package com.myong.backend.domain.dto.designer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DesignerWantedDayRequestDto {

    @NotBlank
    private String wantedDay;
}
