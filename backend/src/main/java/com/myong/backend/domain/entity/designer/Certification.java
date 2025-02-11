package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Certification {

    @Id
    @Column(name = "cr_id")
    private UUID id = UUID.randomUUID(); // 자격증 고유 키

    @Column(name = "ce_name",nullable = false)
    private String name; // 자격증 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ce_id", nullable = false)
    private Resume resume; // 구직 지원서 고유 키

    public Certification(String name) {
        this.name = name;
    }
}
