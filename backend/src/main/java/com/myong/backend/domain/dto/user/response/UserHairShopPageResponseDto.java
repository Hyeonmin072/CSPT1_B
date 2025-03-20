package com.myong.backend.domain.dto.user.response;

import com.myong.backend.domain.dto.user.common.ShopCommonDto;
import lombok.Data;

import java.util.List;

@Data
public class UserHairShopPageResponseDto {

    private String location;
    private List<ShopCommonDto> shops;

    public UserHairShopPageResponseDto(String location, List<ShopCommonDto> shops){
        this.location = location;
        this.shops = shops;
    }
}
