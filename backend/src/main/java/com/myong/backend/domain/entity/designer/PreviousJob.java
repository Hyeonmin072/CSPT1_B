package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
public class PreviousJob {

    @Id
    @Column(name = "pj_id")
    private String id = UUID.randomUUID().toString();

    //근무지 명
    @Column(name = "pj_name")
    private String name;

    //입사일
    @Column(name = "pj_joinDate")
    private LocalDate joinDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jp_id")
    private Resume resume;
}
