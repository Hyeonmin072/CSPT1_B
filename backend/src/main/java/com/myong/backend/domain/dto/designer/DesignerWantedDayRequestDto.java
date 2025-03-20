package com.myong.backend.domain.dto.designer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public class DesignerWantedDayRequestDto {

    @NotBlank
    private String wantedDay;
}
