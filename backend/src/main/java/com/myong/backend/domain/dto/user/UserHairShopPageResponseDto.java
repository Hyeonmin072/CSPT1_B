package com.myong.backend.domain.dto.user;

import com.myong.backend.domain.dto.user.List.ShopListDto;
import lombok.Data;

import java.util.List;

@Data
public class UserHairShopPageResponseDto {

    private String location;
    private List<ShopListDto> shops;

    public UserHairShopPageResponseDto(String location, List<ShopListDto> shops){
        this.location = location;
        this.shops = shops;
    }
}
