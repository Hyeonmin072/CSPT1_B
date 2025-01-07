package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class Attendance {
    //근태아이디
    @Id
    @Column(name = "at_id")
    private String id;

    //근무 상태
    @Column(nullable = false, name = "at_status")
    @Enumerated(EnumType.STRING)
    private Status status = Status.NO;

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
    private Designer designer;

    public Attendance(Status status, LocalDateTime date) {
        this.id = UUID.randomUUID().toString();
        this.status = status;
        this.date = date;
    }
}
