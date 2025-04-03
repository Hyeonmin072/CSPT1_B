package com.myong.backend.domain.dto.job;

import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.shop.Work;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO for {@link com.myong.backend.domain.entity.shop.JobPost}
 */
@Builder
@Value
public class JobPostDetailResponseDto {
    @NotBlank
    String shopName; // 가게 이름

    @NotBlank
    UUID id; // 구인게시물 아이디

    @NotBlank
    String title; // 구인게시물 제목

    String salary; // 구인게시물 급여

    @NotBlank
    Gender gender; // 구인게시물 우대성별

    @NotBlank
    Work work; // 구인게시물 근무기간

    LocalTime workTime; // 구인게시물 출근시간
    LocalTime leaveTime; // 구인게시물 퇴근시간

    @NotBlank
    String content; // 구인게시물 내용
}