package com.myong.backend.domain.dto.reservation.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationPage1ResponseDto {

    private String designerEmail;
    private String designerName;
    private String designerDesc;
    private String designerImage;
    private Double designerRating;
    private Integer designerLike;
    private Integer designerReviewCount;
}
