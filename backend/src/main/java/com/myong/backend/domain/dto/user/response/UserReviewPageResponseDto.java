package com.myong.backend.domain.dto.user.response;


import com.myong.backend.domain.entity.usershop.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class UserReviewPageResponseDto {
    private Double reviewRating;
    private String reviewContent;
    private String reviewImg;
    private String shopEmail;
    private String shopName;
    private String designerName;
    private String designerEmail;

}
