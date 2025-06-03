package com.myong.backend.domain.dto.designer;

import com.myong.backend.domain.dto.designer.ResponseJobPostDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostListResponseDto {
    List<ResponseJobPostDetailDto> jobPosts;//게시글 리스트
    int total; //토탈 게시글 수
    int page; // 현재 페이지 번호
    int pageSize; // 한 페이지당 게시글 수
}
