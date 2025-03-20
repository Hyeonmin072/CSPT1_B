package com.myong.backend.domain.dto.user.request;


import com.myong.backend.domain.dto.user.common.DesignerCommonDto;
import com.myong.backend.domain.dto.user.common.ReviewCommonDto;
import com.myong.backend.domain.dto.user.common.ShopCommonDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ShopDetailsResponseDto {

    private ShopCommonDto shop;
    private List<DesignerCommonDto> designers;
    private List<ReviewCommonDto> reviews;
    private String highestPriceCoupon;
    private List<String> reviewImageUrl;


}
