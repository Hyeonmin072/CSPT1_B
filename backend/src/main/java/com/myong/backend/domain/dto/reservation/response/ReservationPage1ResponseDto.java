package com.myong.backend.domain.dto.reservation.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationPage1ResponseDto {

    private String name;
    private String desc;
    private String imageUrl;
    private Double rating;
    private Integer like;
    private Integer reviewCount;
}
