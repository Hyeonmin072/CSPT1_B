package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.entity.designer.JobPost;
import com.myong.backend.domain.entity.designer.Resume;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
public class Application {

    @Id
    @Column(name = "a_id",nullable = false)
    private String id = UUID.randomUUID().toString();

    //신청한 날짜
    @Column(name = "a_create_date")
    private LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jp_id")
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "re_id")
    private Resume resume;

}
