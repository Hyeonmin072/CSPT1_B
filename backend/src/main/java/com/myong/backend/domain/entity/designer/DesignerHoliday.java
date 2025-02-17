package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class DesignerHoliday {

    @Id
    @Column(name = "dh_id")
    private UUID id = UUID.randomUUID(); //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer;

    @Column(name = "dh_date", nullable = false)
    private LocalDate date;

    public DesignerHoliday(Designer designer, LocalDate date) {
        this.designer = designer;
        this.date = date;
    }
}
