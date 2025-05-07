package com.myong.backend.domain.dto.user.data;


import com.myong.backend.domain.entity.designer.Designer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DesignerListData {

    private String designerEmail;
    private String designerName;
    private String designerDesc;
    private Integer designerLike;
    private Double designerRating;
    private String designerImage;

    public static DesignerListData fromDesigner(Designer designer){
        return DesignerListData.builder()
                .designerEmail(designer.getEmail())
                .designerName(designer.getName())
                .designerDesc(designer.getDesc())
                .designerLike(designer.getLike())
                .designerRating(designer.getRating())
                .designerImage(designer.getImage())
                .build();
    }
}
