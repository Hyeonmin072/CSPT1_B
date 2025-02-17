package com.myong.backend.domain.entity.business;

import com.myong.backend.domain.entity.designer.Designer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Sales {

    @Id
    @Column(name = "sa_id")
    private UUID id = UUID.randomUUID(); // 매출 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id", nullable = false)
    private Payment payment; // 결제 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer; // 디자이너 고유 키

    @Column(name = "sa_amount", nullable = false)
    private Long amount; // 금액

    public Sales(Payment payment, Designer designer, Long amount) {
        this.payment = payment;
        this.designer = designer;
        this.amount = amount;
    }
}
