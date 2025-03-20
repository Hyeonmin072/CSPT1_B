package com.myong.backend.domain.dto.user.List;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DesignerListDto {

    private String name;
    private String desc;
    private Integer like;
    private Double rating;
}
