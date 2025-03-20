package com.myong.backend.domain.dto.user.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewListDto {

    private String menu;
    private String designerName;
    private String userName;
    private String desc;
    private Double rating;

}
