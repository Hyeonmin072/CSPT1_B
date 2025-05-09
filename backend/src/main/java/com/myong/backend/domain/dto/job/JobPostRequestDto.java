package com.myong.backend.domain.dto.job;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class JobPostRequestDto {
    String id;

    @NotBlank
    String title;

    String salary;

    @NotBlank
    String gender;

    @NotBlank
    String work;

    String workTime;
    String leaveTime;

    @NotBlank
    String content;
}