package com.myong.backend.domain.dto.user.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOwnDesignerPageResponseDto {

    private String designerNickname; // 디자이너 닉네임
    private String designerEmail; // 디자이너 이메일
    private String designerImage; // 디자이너 이미지
    private List<String> reviewImage;   // 리뷰 이미지


}
