package com.myong.backend.domain.dto.designer;



import com.myong.backend.domain.dto.designer.data.ReviewData;
import com.myong.backend.domain.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DesignerProfileResponseDto {
    String name;

    String email;

    String nickName;

    String tel;

    String image;

    String backgroundImage;

    String description;

    int age;

    int likeCnt;

    boolean isLike;

    String shopName;

    Gender gender;

    List<ReviewData> reviews;
}
