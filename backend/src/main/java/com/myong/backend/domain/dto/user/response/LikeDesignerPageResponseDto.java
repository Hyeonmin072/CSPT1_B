package com.myong.backend.domain.dto.user.response;


import com.myong.backend.domain.entity.designer.Designer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeDesignerPageResponseDto {

    private String designerEmail;// 디자이너 이메일
    private String designerName; // 디자이너 이름
    private String designerDesc; // 디자이너 설명
    private String shopName;     // 소속가게이름
    private String designerImage;// 디자이너 이미지



}
