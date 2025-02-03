package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
public class DesignerHoliday {

    @Id
    @Column(name = "dh_id")
    private String id = UUID.randomUUID().toString();

    @Column(name = "dh_date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    private Designer designer;
}
