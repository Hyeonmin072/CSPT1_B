package com.myong.backend.domain.dto.reservation.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AvailableTimeResponseDto {

    private String opentime;
    private String closetime;
    private List<LocalTime> availableTimes;
    private List<LocalTime> unavailableTimes;
}
