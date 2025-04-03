package com.myong.backend.domain.dto.job;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * DTO for {@link com.myong.backend.domain.entity.shop.JobPost}
 */
@Value
public class JobPostResponseDto {
    @NotBlank
    String shopName; // 가게 이름

    @NotBlank
    String id; // 구인게시물 아이디

    @NotBlank
    String title; // 구인게시물 제목

    String salary; // 구인게시물 급여

    @NotBlank
    String gender; // 구인게시물 우대성별

    @NotBlank
    String work; // 구인게시물 근무기간

    String workTime; // 구인게시물 출근시간
    String leaveTime; // 구인게시물 퇴근시간
}