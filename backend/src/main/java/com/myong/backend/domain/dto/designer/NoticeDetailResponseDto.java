package com.myong.backend.domain.dto.designer;

import com.myong.backend.domain.entity.shop.Notice;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class NoticeDetailResponseDto {
    private final UUID id;          // 공지사항 아이디
    private final String title;     // 제목
    private final String content;   // 내용
    private final Boolean importance; // 중요 여부
    private final LocalDateTime createDate; // 생성 날짜

    public NoticeDetailResponseDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.importance = notice.getImportance();
        this.createDate = notice.getCreateDate();
    }
}
