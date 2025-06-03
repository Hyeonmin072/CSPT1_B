package com.myong.backend.domain.dto.job;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


@Getter
@NoArgsConstructor
public class JobPostRequestDto {
    @NotBlank
    String title;

    String salary;

    String gender;

    @NotBlank
    String work;

    LocalTime workTime;
    LocalTime leaveTime;

    @NotBlank
    String content;
}