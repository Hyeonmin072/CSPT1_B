package com.myong.backend.domain.dto.user.response;

import com.myong.backend.domain.dto.user.data.DesignerListData;
import com.myong.backend.domain.dto.user.data.DesignerTop4ListData;
import com.myong.backend.domain.dto.user.data.ShopListData;
import com.myong.backend.domain.dto.user.data.ShopTop3ListData;
import com.myong.backend.domain.entity.Advertisement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserHomePageResponseDto {

    private List<ShopTop3ListData> top3Shops;
    private List<DesignerListData> top4Designers;
    private List<Advertisement> advertisements;


}
