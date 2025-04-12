package com.myong.backend.domain.dto.user.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopTop3ListData {

    private String shopName;
    private String shopDesc;
    private Integer shopReviewCount;
    private Double shopRating;
    private String shopThumbnail;
}
