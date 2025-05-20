package com.myong.backend.domain.dto.reservation.response;


import com.myong.backend.domain.dto.reservation.MenuListData;
import com.myong.backend.domain.entity.shop.MenuCategory;
import lombok.Data;

import java.util.List;

@Data

public class SelectMenuResponseDto {

    private List<MenuListData> recommendedMenus ; // 추천메뉴
    private List<MenuListData> cutMenus; // 커트메뉴
    private List<MenuListData> permMenus; // 파마메뉴
    private List<MenuListData> dyeingMenus; // 염색메뉴
    private List<MenuListData> clinicMenus; // 클리닉메뉴
    private List<MenuListData> stylingMenus; // 스타일링메뉴


    private String shopName;
    private String shopEmail;

    public SelectMenuResponseDto(List<MenuListData>[] responseMenus ,List<MenuListData> recommendedMenus, String shopName, String shopEmail){
        this.recommendedMenus = recommendedMenus;

        for(MenuCategory category : MenuCategory.values()){
            if(category == MenuCategory.NONE){continue;}
            switch(category){
                case CUT ->  this.cutMenus=responseMenus[category.ordinal()];
                case PERM -> this.permMenus=responseMenus[category.ordinal()];
                case DYEING -> this.dyeingMenus=responseMenus[category.ordinal()];
                case CLINIC -> this.clinicMenus=responseMenus[category.ordinal()];
                case STYLING -> this.stylingMenus=responseMenus[category.ordinal()];
            }
        }
        this.shopName = shopName;
        this.shopEmail = shopEmail;
    }

}
