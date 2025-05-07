package com.myong.backend.domain.dto.user.response;


import com.myong.backend.domain.dto.user.data.DesignerListData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DesignerPageResponseDto {
    private List<DesignerListData> topDesigners;
    private List<DesignerListData> hotDesigners;
    private List<DesignerListData> designersForUser;
}
