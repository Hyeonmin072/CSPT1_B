package com.myong.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
public class Advertisement {
    //광고 고유키
    @Column(name = "ad_id")
    @Id
    private String id = UUID.randomUUID().toString();

    //이미지 경로
    @Column(nullable = false, name = "ad_image")
    private String image;

    //기한
    @Column(updatable = false, nullable = false, name = "ad_expires")
    //갱신시간일 경우 updatable은 false로 변경
    private LocalDateTime expires;
}
