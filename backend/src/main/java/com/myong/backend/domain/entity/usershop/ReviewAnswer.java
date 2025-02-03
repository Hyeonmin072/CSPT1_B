package com.myong.backend.domain.entity.usershop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAnswer {
    @Id
    @Column(name = "rva_id")
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rv_id", nullable = false)
    private Review review;

    @Column(name = "rva_create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "rva_content", nullable = false)
    private String content;
}
