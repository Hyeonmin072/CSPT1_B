package com.myong.backend.domain.dto.user;

import com.myong.backend.domain.entity.Advertisement;
import com.myong.backend.domain.entity.shop.Shop;
import lombok.Data;

import java.util.List;

@Data
public class UserHomePageResponseDto {

    private String location;
    private List<UserHomePageShopListDto> shops;
    private List<Advertisement> advertisements;

    public UserHomePageResponseDto(String location,List<UserHomePageShopListDto> shops,List<Advertisement> advertisements){
        this.location = location;
        this.shops = shops;
        this.advertisements = advertisements;
    }
}
