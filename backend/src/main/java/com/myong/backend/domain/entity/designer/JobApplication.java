package com.myong.backend.domain.entity.designer;

import com.myong.backend.domain.entity.shop.JobPost;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class JobApplication {
 
    @Id
    @Column(name ="ap_id")
    private String id; // 구직 지원서 고유 키

    @Column(name = "ap_wantday", nullable = false)
    private String wantDay; // 희망 근무요일

    @Column(name = "ap_content")
    private String content; // 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer; // 디자이너 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jp_id", nullable = false)
    private JobPost jobPost; // 구인 게시물 고유 키

    public JobApplication(String id, String wantDay, Designer designer, JobPost jobPost) {
     this.id = id;
     this.wantDay = wantDay;
     this.designer = designer;
     this.jobPost = jobPost;
    }
}
