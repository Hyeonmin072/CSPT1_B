package com.myong.backend.domain.dto.designer;

import com.myong.backend.domain.entity.designer.Exp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeRequestDto {

    private String content;

    private Exp exp;

    private String portfolio;

    private String image;

    private List<CareerRequestDto> careers = new ArrayList<>();

    private List<CertificationRequestDto> certifications = new ArrayList<>();

    private List<DesignerWantedDayRequestDto> wantedDays = new ArrayList<>();

    public void setImage(String newImageUrl) {
    }
}
