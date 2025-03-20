package com.myong.backend.domain.dto.user;

import com.myong.backend.domain.dto.user.common.ShopCommonDto;
import com.myong.backend.domain.entity.Advertisement;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserHomePageResponseDto {

    private String location;
    private List<ShopCommonDto> shops;
    private List<Advertisement> advertisements;


}
