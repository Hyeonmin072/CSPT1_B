package com.myong.backend.domain.dto.user;

import com.myong.backend.domain.dto.user.List.ShopListDto;
import com.myong.backend.domain.entity.Advertisement;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserHomePageResponseDto {

    private String location;
    private List<ShopListDto> shops;
    private List<Advertisement> advertisements;


}
