package com.myong.backend.domain.entity.designer;

import com.myong.backend.domain.entity.shop.Application;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Resume {
    //구직 지원서 고유키
    @Id
    @Column(name ="re_id")
    private String id = UUID.randomUUID().toString();

    //희망 근무요일
    @Column(nullable = false, name = "re_wantday")
    private String wantDay;

    //내용
    @Column(name = "re_content")
    private String content;

    //경력여부
    @Column(name = "re_exp")
    private String exp;

    //포트폴리오
    @Column(name = "re_portfolio")
    private String portfolio;

    //해당 이력서의 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    private Designer designer;

    //구인글에 신청한 이력서들
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

    //이전 직장
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<PreviousJob> previousJobs = new ArrayList<>();
}
