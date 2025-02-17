package com.myong.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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
    private LocalDate expireDate;

    //생성일
    @CreatedDate
    @Column(name = "ad_create_date", updatable = false)
    private LocalDate createDate;

    public Advertisement(String image, LocalDate expireDate) {
        this.image = image;
        this.expireDate = expireDate;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Advertisement that = (Advertisement) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
