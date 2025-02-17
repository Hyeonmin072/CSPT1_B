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
    @Id
    @Column(name = "ad_id")
    private UUID id = UUID.randomUUID();

    //이미지 경로
    @Column(name = "ad_image", nullable = false)
    private String image;

    //기한
    @Column(name = "ad_expire_date", nullable = false, updatable = false)
    private LocalDateTime expireDate;

    public Advertisement(String image, LocalDateTime expireDate) {
        this.image = image;
        this.expireDate = expireDate;
    }
}
