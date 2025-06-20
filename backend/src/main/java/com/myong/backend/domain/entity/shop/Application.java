package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.entity.designer.Resume;
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
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ap_id")
    private UUID id; // 구인구직신청 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jp_id", nullable = false)
    private JobPost jobPost; // 구인 게시물 고유 키

    @CreatedDate
    @Column(name = "ap_create_date", updatable = false)
    private LocalDateTime createDate; // 생성일(신청날짜)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "re_id", nullable = false)
    private Resume resume; // 구직 지원서 고유 키


    public Application(JobPost jobPost) {
        this.jobPost = jobPost;
        this.resume = resume;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Application that = (Application) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
