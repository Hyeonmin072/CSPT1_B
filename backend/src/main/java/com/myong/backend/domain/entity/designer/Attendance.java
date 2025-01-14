package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

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
    private Status status;

    //출근일시
    @Column(name = "at_in")
    private LocalTime in;

    //퇴근일시
    @Column(name = "at_out")
    private LocalTime out;

    //근무 시간
    @Column(name = "at_times")
    private LocalTime times;

    //근무 날짜
    @Column(nullable = false, name = "at_date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    private Designer designer;

    public Attendance() {
        this.id = UUID.randomUUID().toString();
        this.status = Status.NO;
        this.date = LocalDate.now();
    }

}
