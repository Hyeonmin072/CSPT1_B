package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
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
    @Column(name = "at_work_time")
    private LocalTime workTime;

    //퇴근일시
    @Column(name = "at_leave_time")
    private LocalTime leaveTime;

    //생성일(근무 날짜)
    @CreatedDate
    @Column(name = "at_date", updatable = false)
    private LocalDate date;
    
    //비고
    @Column(name = "at_note", nullable = false)
    private String note = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    private Designer designer;

    @Builder
    public Attendance(LocalTime workTime, LocalTime leaveTime, Designer designer) {
        this.workTime = workTime;
        this.leaveTime = leaveTime;
        this.designer = designer;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Attendance that = (Attendance) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
