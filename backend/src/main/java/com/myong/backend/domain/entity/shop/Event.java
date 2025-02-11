package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.entity.user.DiscountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Event {

    @Id
    @Column(name = "e_id")
    private UUID id = UUID.randomUUID(); // 이벤트 고유 키

    @Column(name = "e_discount")
    private Long amount; // 이벤트 할인 금액 
    
    @Column(name = "e_type")
    private DiscountType type; // 이벤트 할인 타입
    
    @Column(name = "e_start_date")
    private LocalDate startDate; // 이벤트 시작일
    
    @Column(name = "e_end_date")
    private LocalDate endDate; // 이벤트 종료일

    public Event(Long amount, DiscountType type, LocalDate startDate, LocalDate endDate) {
        this.amount = amount;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
