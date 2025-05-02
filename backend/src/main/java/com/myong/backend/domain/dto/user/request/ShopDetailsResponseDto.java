package com.myong.backend.domain.dto.user.request;


import com.myong.backend.domain.dto.user.data.DesignerListData;
import com.myong.backend.domain.dto.user.data.ReviewListData;
import com.myong.backend.domain.dto.user.data.ShopListData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ShopDetailsResponseDto {

    private ShopListData shop;
    private List<DesignerListData> designers;
    private List<ReviewListData> reviews;
    private String highestPriceCoupon;
    private List<String> reviewImageUrl;


}
