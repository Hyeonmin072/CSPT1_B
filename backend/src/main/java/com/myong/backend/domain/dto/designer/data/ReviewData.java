package com.myong.backend.domain.dto.designer.data;


import com.myong.backend.domain.entity.usershop.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@Data
public class ReviewData {

    private String userName; // 리뷰받은 유저 이름
    private String reviewImage; // 리뷰 이미지
    private Double reviewRating; // 리뷰 레이팅
    private String reviewContent; // 리뷰 내용
    private String menuName; // 메뉴 이름

    public ReviewData (String userName, String reviewImage, Double reviewRating,
                       String reviewContent, String menuName){
        this.userName = userName;
        this.reviewImage = reviewImage;
        this.reviewRating = reviewRating;
        this.reviewContent = reviewContent;
        this.menuName = menuName;
    }
}
