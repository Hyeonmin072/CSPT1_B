package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.entity.user.DiscountType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Event {

    @Id
    @Column(name = "e_id")
    private UUID id = UUID.randomUUID(); // 이벤트 고유 키
    
    @Column(name = "e_name")
    private String name = ""; // 이벤트 이름

    @Column(name = "e_discount")
    private Long amount = 0L; // 이벤트 할인 금액
    
    @Column(name = "e_type", nullable = false)
    private DiscountType type; // 이벤트 할인 타입
    
    @Column(name = "e_start_date")
    private LocalDate startDate = LocalDate.of(9999,9,9); // 이벤트 시작일 9999-09-09

    @Column(name = "e_end_date")
    private LocalDate endDate = LocalDate.of(9999,9,9); // 이벤트 종료일 9999-09-09

    @ManyToOne
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    @Builder
    public Event(String name, Long amount, DiscountType type, LocalDate startDate, LocalDate endDate, Shop shop) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.shop = shop;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Event event = (Event) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
