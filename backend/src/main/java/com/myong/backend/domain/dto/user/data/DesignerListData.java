package com.myong.backend.domain.dto.user.data;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DesignerListData {

    private String designerEmail;
    private String designerName;
    private String designerDesc;
    private Integer designerLike;
    private Double designerRating;
    private String designerImage;
}
