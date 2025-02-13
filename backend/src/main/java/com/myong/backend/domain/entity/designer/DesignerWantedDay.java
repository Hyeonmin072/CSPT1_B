package com.myong.backend.domain.entity.designer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
}
