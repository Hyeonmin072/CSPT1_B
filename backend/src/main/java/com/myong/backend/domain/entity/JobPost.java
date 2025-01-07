package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class JobPost {
    //구인게시물고유키
    @Id
    @Column(name = "jp_id")
    private String id;

    //제목
    @Column(name = "jp_title", nullable = false)
    private String title;

    //내용
    @Column(name = "jp_content")
    private String content;

    //요구 경력
    @Column(name = "jp_exp")
    private long exp;

    // 근무 형태
    @Column(name = "jp_work",nullable = false)
    @Enumerated(EnumType.STRING)
    private Work work = Work.FULLTIME; //정규직 디폴트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop;

    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobApplication> JobApplications = new ArrayList<>();

    public JobPost(String title, Work work, Shop shop) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.work = work;
        this.shop = shop;
    }

}
