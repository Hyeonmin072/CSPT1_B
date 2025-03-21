package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Career {

    @Id
    @Column(name = "cr_id")
    private UUID id = UUID.randomUUID(); // 이전 근무지 고유 키

    //근무지 이름
    @Column(name = "cr_name",nullable = false)
    private String name;

    //입사일
    @Column(name = "cr_join_date", nullable = false)
    private LocalDate joinDate;

    //퇴사일
    @Column(name = "cr_out_date")
    private LocalDate outDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jp_id", nullable = false)
    private Resume resume; // 구직 지원서 고유 키

    public Career(String name, LocalDate joinDate, Resume resume) {
        this.name = name;
        this.joinDate = joinDate;
        this.resume = resume;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Career career = (Career) o;
        return getId() != null && Objects.equals(getId(), career.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
