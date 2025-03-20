package com.myong.backend.domain.entity.designer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.DayOfWeek;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DesignerWantedDay {
    @Id
    @Column(name = "wt_id")
    private UUID id = UUID.randomUUID();

    @Column(name = "wt_day", nullable = false)
    private DayOfWeek wantedDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="re_id", nullable = false)
    @JsonBackReference
    private Resume resume; // 구직 지원서 고유 키

    public DesignerWantedDay(DayOfWeek wantedDay) {
        this.wantedDay = wantedDay;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        DesignerHoliday that = (DesignerHoliday) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void updateWantedDay(DayOfWeek wantedDay) {
        this.wantedDay = wantedDay;
    }

    public void updateResume(Resume resume) {
        this.resume = resume;
    }
}
