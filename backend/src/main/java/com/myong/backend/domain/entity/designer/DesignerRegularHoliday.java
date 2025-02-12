package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.DayOfWeek;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class DesignerRegularHoliday {

    @Id
    @Column(name = "drh_id")
    private UUID id = UUID.randomUUID(); // 디자이너 정기휴무일 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer; // 디자이너 고유 키

    @Column(name = "drh_day", nullable = false)
    private DayOfWeek day; // 정기휴무 요일

    public DesignerRegularHoliday(Designer designer, DayOfWeek day) {
        this.designer = designer;
        this.day = day;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        DesignerRegularHoliday that = (DesignerRegularHoliday) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
