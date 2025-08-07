package com.myong.backend.domain.dto.job;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationResponseDto {
    private String designerName;
    private String designerDesc;
    private String designerImage;
    private String designerEmail;
    private String designerGender;
    private Integer designerLike;
}
