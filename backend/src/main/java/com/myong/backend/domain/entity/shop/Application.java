package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.entity.designer.JobPost;
import com.myong.backend.domain.entity.designer.Resume;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Application {

    @Id
    @Column(name = "ap_id")
    private UUID id = UUID.randomUUID(); // 구인구직신청 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jp_id", nullable = false)
    private JobPost jobPost; // 구인 게시물 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "re_id", nullable = false)
    private Resume resume; // 구직 지원서 고유 키

    @Column(name = "ap_create_date", nullable = false)
    @CreatedDate
    private LocalDateTime createDate; // 신청날짜

    public Application(JobPost jobPost) {
        this.jobPost = jobPost;
        this.resume = resume;
    }
}
