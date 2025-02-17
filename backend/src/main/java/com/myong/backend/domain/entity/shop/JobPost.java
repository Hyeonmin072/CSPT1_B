package com.myong.backend.domain.entity.designer;

import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.shop.Application;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.shop.Work;
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
    //구인 게시물 고유키
    @Id
    @Column(name = "jp_id")
    private UUID id = UUID.randomUUID();

    //제목
    @Column(name = "jp_title", nullable = false)
    private String title;

    //내용
    @Column(name = "jp_content", nullable = false)
    private String content;

    // 근무 형태
    @Column(name = "jp_work", nullable = false)
    @Enumerated(EnumType.STRING)
    private Work work = Work.FULLTIME; //정규직 디폴트

    //급여
    @Column(name = "jp_salary")
    private String salary;

    //요구 성별
    @Column(name = "jp_gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    //요구 정시출근시간
    @Column(name = "jp_worktime", nullable = false)
    private String workTime;

    //요구 정시퇴근시간
    @Column(name = "jp_leavetime", nullable = false)
    private String leaveTime;

    //첨부파일
    @Column(name = "jp_file")
    private String file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    @OneToMany(mappedBy = "jobPost",cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>(); // 이 구인글에 지원된 지원서들


    public JobPost(String title, String content, Work work, String workTime, String leaveTime, Shop shop) {
        this.title = title;
        this.content = content;
        this.work = work;
        this.workTime = workTime;
        this.leaveTime = leaveTime;
        this.shop = shop;
    }
}
