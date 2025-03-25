package com.myong.backend.domain.dto.user.response;

import com.myong.backend.domain.dto.user.data.ShopListData;
import lombok.Data;

import java.util.List;

@Data
public class UserHairShopPageResponseDto {

    private String location;
    private List<ShopListData> shops;

    public UserHairShopPageResponseDto(String location, List<ShopListData> shops){
        this.location = location;
        this.shops = shops;
    }
}
