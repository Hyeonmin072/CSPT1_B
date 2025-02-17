package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class DesignerRegularHoliday {

    @Id
    @Column(name = "drh_id")
    private UUID id = UUID.randomUUID(); // 디자이너 정기휴무일 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer; // 디자이너 고유 키

    @Column(name = "drh_day", nullable = false)
    private DayOfWeek day; // 정기휴무 요일

    public DesignerRegularHoliday(Designer designer, DayOfWeek day) {
        this.designer = designer;
        this.day = day;
    }
}
