package com.myong.backend.domain.dto.job;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for {@link com.myong.backend.domain.entity.shop.JobPost}
 */
@Value
public class JobPostRequestDto {
    String id;

    @NotBlank
    String title;

    String salary;

    @NotBlank
    String gender;

    @NotBlank
    String work;

    LocalTime workTime;
    LocalTime leaveTime;

    @NotBlank
    String content;
}