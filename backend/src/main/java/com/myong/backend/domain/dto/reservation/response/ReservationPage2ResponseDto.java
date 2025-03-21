package com.myong.backend.domain.dto.reservation.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReservationPage2ResponseDto {

    private String designerName;
    private String designerDesc;
    private String designerIamge;
    private String designerRegularHoliday;
    private List<LocalDate> designerHolidays;
    private List<LocalTime> availableTime;
    private List<LocalTime> unavailableTime;
}
