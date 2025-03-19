package com.myong.backend.domain.entity.designer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Certification {

    @Id
    @Column(name = "cr_id")
    private UUID id = UUID.randomUUID(); // 자격증 고유 키

    @Column(name = "cr_name",nullable = false)
    private String name; // 자격증 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "re_id", nullable = false)
    @JsonBackReference
    private Resume resume; // 구직 지원서 고유 키

    public Certification(String name) {
        this.name = name;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Certification that = (Certification) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateResume(Resume resume) {
        this.resume = resume;
    }
}
