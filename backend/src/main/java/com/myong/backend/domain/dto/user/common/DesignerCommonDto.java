package com.myong.backend.domain.dto.user.common;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DesignerCommonDto {

    private String name;
    private String desc;
    private Integer like;
    private Double rating;
}
