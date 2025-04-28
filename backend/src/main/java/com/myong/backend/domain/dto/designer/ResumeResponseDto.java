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

    public static ResumeResponseDto of(Designer designer, Resume resume) {
        //currentYear(올해 받아오기)
        int cy = java.time.LocalDate.now().getYear();
        int birth = Integer.parseInt(designer.getBirth().toString().substring(0,4));
        int age = cy - birth;

        return ResumeResponseDto.builder()
                .image(resume.getImage())
                .name(designer.getName())
                .tel(designer.getTel())
                .gender(designer.getGender())
                .age(age)
                .exp(resume.getExp())
                .content(resume.getContent())
                .careers(resume.getCareers())
                .certifications(resume.getCertifications())
                .wantedDays(resume.getWantedDays())
                .build();
    }
}
