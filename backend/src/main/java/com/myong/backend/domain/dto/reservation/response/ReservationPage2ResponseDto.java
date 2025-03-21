package com.myong.backend.domain.dto.reservation.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReservationPage2ResponseDto {

    private String designerName;  //디자이너이름
    private String designerDesc;  // 디자이너설명
    private String designerIamge; //디자이너 사진
    private String designerRegularHoliday; // 디자이너 정기휴일
    private List<LocalDate> designerHolidays; // 디자이너 지정휴일
    private List<LocalTime> availableTime; // 예약가능시간
    private List<LocalTime> unavailableTime; // 예약불가능시간
}
