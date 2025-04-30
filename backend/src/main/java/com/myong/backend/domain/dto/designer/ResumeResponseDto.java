package com.myong.backend.domain.dto.designer;


import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.designer.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeResponseDto {
    private String name;

    private String email;

    private String tel;

    private Gender gender;

    private String image;

    private int age;

    private String content;

    private Exp exp;

    private List<Career> careers;

    private List<Certification> certifications;

    private List<DesignerWantedDay> wantedDays;

}
