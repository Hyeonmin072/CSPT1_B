package com.myong.backend.domain.dto.job;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.myong.backend.domain.entity.shop.JobPost}
 */
@Value
public class JobPostEditDto implements Serializable {
    @NotBlank
    String shopEmail;

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