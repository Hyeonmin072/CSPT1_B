package com.myong.backend.domain.dto.user.data;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DesignerListData {

    private String name;
    private String desc;
    private Integer like;
    private Double rating;
}
