package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Sales {

    // Payment 결제 테이블 기본키를 참조해서 할인 테이블 기본키 생성
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id", nullable = false)
    private Payment payment; // 결제 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer; // 디자이너 고유 키

    @Column(name = "s_amount", nullable = false)
    private Long amount; // 금액

    public Sales(Payment payment, Designer designer, Long amount) {
        this.payment = payment;
        this.designer = designer;
        this.amount = amount;
    }
}
