package com.myong.backend.domain.dto.reservation;


import com.myong.backend.domain.entity.user.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MenuListData {
    private String menuId;
    private String menuName;
    private Integer menuPrice;
    private Integer discountPrice;
    private String discountType;
    private String menuDesc;
    private String menuImage;
}
