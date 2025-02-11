package com.myong.backend.domain.entity.designer;

import com.myong.backend.domain.entity.shop.Application;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Resume {

    //구직 지원서 고유키
    @Id
    @Column(name ="re_id")
    private UUID id = UUID.randomUUID();

    //희망 근무요일
    @Column(name = "re_wantday", nullable = false)
    private String wantDay;

    //내용
    @Column(name = "re_content")
    private String content;

    //경력여부
    @Column(name = "re_exp", nullable = false)
    private String exp;

    //포트폴리오
    @Column(name = "re_portfolio")
    private String portfolio;

    //해당 이력서의 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer;

    //이 지원서로 지원한 구인글
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

    // 경력들
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Career> careers = new ArrayList<>();

    // 자격증들
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Certification> certifications = new ArrayList<>();


    public Resume(String wantDay, String exp, Designer designer) {
        this.wantDay = wantDay;
        this.exp = exp;
        this.designer = designer;
    }
}
