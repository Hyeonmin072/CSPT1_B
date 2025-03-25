package com.myong.backend.domain.dto.designer;


import com.myong.backend.domain.dto.user.common.ReviewCommonDto;
import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.usershop.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponseDto {
    String name;

    String email;

    String nickName;

    String tel;

    String description;

    int age;

    int like;

    String shopName;

    Gender gender;

    List<Review> reviews;
}
