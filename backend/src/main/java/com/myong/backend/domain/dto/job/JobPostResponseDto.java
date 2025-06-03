package com.myong.backend.domain.dto.job;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.time.LocalTime;

@Value
@Builder
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

    LocalTime workTime; // 구인게시물 출근시간
    LocalTime leaveTime; // 구인게시물 퇴근시간
}