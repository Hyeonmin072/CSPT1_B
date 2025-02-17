package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class DesignerWantedDay {
    @Id
    @Column(name = "wt_id")
    private UUID id = UUID.randomUUID();

    @Column(name = "wt_day")
    private DayOfWeek wantedDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jp_id", nullable = false)
    private Resume resume; // 구직 지원서 고유 키
}
