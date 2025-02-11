package com.myong.backend.domain.entity.usershop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReviewAnswer {

    @Id
    @Column(name = "rva_id")
    private UUID id = UUID.randomUUID(); // 리뷰 답변 고유 키

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rv_id", nullable = false)
    private Review review; // 리뷰 고유 키

    @CreatedDate
    @Column(name = "rva_create_date", updatable = false)
    private LocalDateTime createDate; // 리뷰 답변 작성일

    @Column(name = "rva_content", nullable = false)
    private String content; // 리뷰 답변 내용

    public ReviewAnswer(Review review, String content) {
        this.review = review;
        this.content = content;
    }
}
