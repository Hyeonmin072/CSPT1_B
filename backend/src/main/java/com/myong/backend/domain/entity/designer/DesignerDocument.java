package com.myong.backend.domain.entity.designer;


import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.shop.Shop;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Document(indexName = "designer")
@Builder
public class DesignerDocument {

    @Id
    private UUID id;

    private String name;

    private String email;

    private String desc;

    private String tel;

    private String image;

    private String backgroundImage;

    private Gender gender;

    private LocalDate brith;

    private Integer like;

    private Double rating;

    private Integer reviewCount;

    private LocalTime workTime;

    private LocalTime leaveTime;

    private Shop shop; //소속가게

    public static DesignerDocument from (Designer designer){
        return DesignerDocument.builder()
                .id(designer.getId())
                .name(designer.getName())
                .email(designer.getEmail())
                .desc(designer.getDesc())
                .tel(designer.getTel())
                .image(designer.getImage())
                .backgroundImage(designer.getBackgroundImage())
                .gender(designer.getGender())
                .brith(designer.getBirth())
                .like(designer.getLike())
                .rating(designer.getRating())
                .reviewCount(designer.getReviewCount())
                .workTime(designer.getWorkTime())
                .leaveTime(designer.getLeaveTime())
                .shop(designer.getShop())
                .build();

    }

}
