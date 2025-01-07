package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Application {
   //구직 지원서 고유키
    @Id
    @Column(name ="ap_id")
    private String id;

    //희망 근무요일
    @Column(nullable = false, name = "ap_wantday")
    private String wantDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    private Designer designer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jp_id")
    private JobPost jobPost;
}
