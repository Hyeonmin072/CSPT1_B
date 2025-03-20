package com.myong.backend.domain.dto.user.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewCommonDto {

    private String menu;
    private String designerName;
    private String userName;
    private String content;
    private Double rating;

}
