package com.myong.backend.domain.dto.user.response;

import com.myong.backend.domain.dto.user.data.ShopListData;
import com.myong.backend.domain.entity.Advertisement;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserHomePageResponseDto {

    private String userName;
    private String userEmail;
    private String location;
    private List<ShopListData> shops;
    private List<Advertisement> advertisements;


}
