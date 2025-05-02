package com.myong.backend.domain.dto.user.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DesignerPageResponseDto {

    private String designerName; // 디자이너이름
    private String desc;         // 디자이너 설명
    private String shopName;     // 소속가게이름
    private String designerIamge;// 디자이너 이미지

}
