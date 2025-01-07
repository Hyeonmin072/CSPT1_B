package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity

public class Attendance {
    //근태아이디
    @Id
    @Column(name = "at_id")
    private String id;

    //근무 상태
    @Column(nullable = false, name = "at_status")
    @Enumerated(EnumType.STRING)
    Status status = Status.NO;

    //출근일시
    @Column(name = "at_in")
    private LocalDateTime in;

    //퇴근일시
    @Column(name = "at_out")
    private LocalDateTime out;

    //근무 시간
    @Column(name = "at_times")
    private LocalDate times;

    //근무 날짜
    @Column(nullable = false ,name = "at_date")
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    Designer designer;
}
