package com.myong.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class Advertisement {
    //광고 고유키
    @Column(name = "ad_id")
    @Id
    private String id;

    //이미지 경로
    @Column(nullable = false, name = "ad_image")
    private String image;

    //기한
    @Column(updatable = false, nullable = false, name = "ad_expires")
    private LocalDateTime expires;

    public Advertisement(String image, LocalDateTime expires) {
        this.id = UUID.randomUUID().toString();
        this.image = image;
        this.expires = expires;
    }
}
