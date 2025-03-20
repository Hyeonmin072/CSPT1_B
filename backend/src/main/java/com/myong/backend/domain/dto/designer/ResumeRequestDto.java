package com.myong.backend.domain.dto.designer;

import com.myong.backend.domain.entity.designer.Exp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public class ResumeRequestDto {

    private String content;

    private Exp exp;

    private String portfolio;

    private String image;

    private List<CareerRequestDto> careers = new ArrayList<>();

    private List<CertificationRequestDto> certificates = new ArrayList<>();

    private List<DesignerWantedDayRequestDto> wantedDays = new ArrayList<>();
}
