package com.myong.backend.domain.dto.user.response;

import com.myong.backend.domain.dto.user.data.ShopListData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserHairShopPageResponseDto {

    private String location;                // 유저 위치 데이터
    private List<ShopListData> shops;       // 가게 데이터
    private long registeredShopCnt;      // 등록된 헤어샵 갯수
    private long registeredDesignerCnt;  // 등록된 디자이너 갯수
    private long registeredReviewCnt;    // 등록된 리뷰 갯수

}
