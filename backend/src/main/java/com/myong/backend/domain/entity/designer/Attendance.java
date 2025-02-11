package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    //생성일(근무 날짜)
    @CreatedDate
    @Column(name = "at_date", updatable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    private Designer designer;

}
