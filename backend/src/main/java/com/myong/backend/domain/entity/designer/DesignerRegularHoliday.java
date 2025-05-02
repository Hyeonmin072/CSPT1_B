package com.myong.backend.domain.entity.designer;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class DesignerRegularHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "drh_id")
    private UUID id; // 디자이너 정기휴무일 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer; // 디자이너 고유 키


    @Enumerated(EnumType.STRING)
    @Column(name = "drh_day", nullable = false)
    private RegularHoliday day = RegularHoliday.NONE; // 정기휴무 요일

    @Builder
    public DesignerRegularHoliday(Designer designer) {
        this.designer = designer;
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

    public void updateHoliday(RegularHoliday regularHoliday) {
        if(this.day != regularHoliday) this.day = regularHoliday;
    }
}
