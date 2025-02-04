package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
public class DesignerRegularHoliday {

    @Id
    @Column(name = "drh_id")
    private String id = UUID.randomUUID().toString();

    @Column(name = "drh_day")
    private String day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    private Designer designer;

}
