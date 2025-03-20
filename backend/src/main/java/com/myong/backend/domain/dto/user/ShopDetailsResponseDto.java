package com.myong.backend.domain.dto.user;


import com.myong.backend.domain.dto.user.List.DesignerListDto;
import com.myong.backend.domain.dto.user.List.ReviewListDto;
import com.myong.backend.domain.dto.user.List.ShopListDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ShopDetailsResponseDto {

    private List<ShopListDto> shops;
    private List<DesignerListDto> designers;
    private List<ReviewListDto> reviews;
    private String highestPriceCoupon;
    private String reviewImageUrl;


}
