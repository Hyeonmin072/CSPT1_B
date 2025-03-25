package com.myong.backend.domain.dto.reservation.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AvailableTimeResponseDto {

    private List<LocalTime> availableTimes;
    private List<LocalTime> unavailableTimes;
}
