package com.myong.backend.domain.dto.designer;

import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.shop.Work;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseJobPostDetailDto {
    private UUID postId;        // 게시글 아이디
    private String title;       // 제목
    private String content;     // 소개
    private String file;       // 첨부파일
    private Gender gender;      // 성별
    private String shopName;    // 가게 이름
    private Work work;          // 근무 형태
    private String salary;      // 급여 정보
    private String address;     // 주소
    private String imageUrl;    // 썸네일 이미지 URL
    private LocalTime workTime; //출근시간
    private LocalTime leaveTime;//퇴근시간
    private String postedAgo;   // 등록된 시간 (예: "5분 전")
}
