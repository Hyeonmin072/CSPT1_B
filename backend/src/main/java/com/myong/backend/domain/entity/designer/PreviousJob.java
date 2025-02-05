package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class PreviousJob {

    @Id
    @Column(name = "pj_id")
    private UUID id = UUID.randomUUID(); // 이전 근무지 고유 키

    //근무지 이름
    @Column(name = "pj_name",nullable = false)
    private String name;

    //입사일
    @Column(name = "pj_joinDate",nullable = false)
    private LocalDate joinDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jp_id", nullable = false)
    private Resume resume; // 구직 지원서 고유 키

    public PreviousJob(String name, LocalDate joinDate, Resume resume) {
        this.name = name;
        this.joinDate = joinDate;
        this.resume = resume;
    }
}
