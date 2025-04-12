package com.myong.backend.domain.dto.user.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DesignerTop4ListData {

    private String designerName;
    private String designerDesc;
    private Double designerRating;
    private String designerImage;
}
