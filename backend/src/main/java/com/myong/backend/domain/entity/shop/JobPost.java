package com.myong.backend.domain.entity.designer;

import com.myong.backend.domain.entity.shop.Application;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.shop.Work;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
public class JobPost {
    //구인게시물고유키
    @Id
    @Column(name = "jp_id")
    private String id = UUID.randomUUID().toString();

    //제목
    @Column(name = "jp_title", nullable = false)
    private String title;

    //내용
    @Column(name = "jp_content")
    private String content;

    // 근무 형태
    @Column(name = "jp_work")
    @Enumerated(EnumType.STRING)
    private Work work = Work.FULLTIME; //정규직 디폴트

    //급여
    @Column(name = "jp_salary")
    private String salary;

    //요구 성별
    @Column(name = "jp_gender")
    private String gender;

    //요구출근시간
    @Column(name = "jp_worktime")
    private String workTime;

    //요구퇴근시간
    @Column(name = "jp_leavetime")
    private String leaveTime;

    //첨부파일
    @Column(name = "jp_file")
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id")
    private Shop shop;

    @OneToMany(mappedBy = "jobPost",cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

}
