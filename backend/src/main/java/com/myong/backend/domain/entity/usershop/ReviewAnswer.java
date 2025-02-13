package com.myong.backend.domain.entity.usershop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ReviewAnswer that = (ReviewAnswer) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
