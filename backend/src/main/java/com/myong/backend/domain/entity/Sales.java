package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Sales {

    // Payment 결제 테이블 기본키를 참조해서 할인 테이블 기본키 생성
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id")
    Payment payment;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "d_id")
//    Designer designer;

    @Column(name = "amount")
    private Long amount;

    public Sales(){

    }

}
