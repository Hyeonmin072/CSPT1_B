package com.myong.backend.domain.dto.reservation;


import com.myong.backend.domain.entity.user.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MenuListData {
    private String name;
    private Integer price;
    private Integer discountPrice;
    private String discountType;
    private String desc;
    private String image;
}
