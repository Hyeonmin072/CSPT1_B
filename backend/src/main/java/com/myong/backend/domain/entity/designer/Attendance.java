package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class Attendance {
    //근태아이디
    @Id
    @Column(name = "at_id")
    private UUID id = UUID.randomUUID();

    //근무 상태
    @Column(name = "at_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.NO;;

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
    @CreatedDate
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    private Designer designer;

}
