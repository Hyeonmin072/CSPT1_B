package com.myong.backend.domain.dto.user.response;


import com.myong.backend.domain.entity.shop.MenuCategory;
import com.myong.backend.domain.entity.usershop.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String menuName;
    private LocalDateTime createdAt;
    private String reply;
    private MenuCategory category;

}
