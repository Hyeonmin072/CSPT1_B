package com.myong.backend.domain.dto.user.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewListData {

    private String menu;
    private String designerName;
    private String userName;
    private String content;
    private Double rating;

}
