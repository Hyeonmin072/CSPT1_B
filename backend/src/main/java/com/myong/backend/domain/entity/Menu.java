package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter

public class Menu {
    //메뉴아이디
    @Id
    @Column(name = "m_id")
    private String id = UUID.randomUUID().toString();

    //메뉴 이름
    @Column(nullable = false, name = "m_name")
    private String name;

    //메뉴 설명
    @Column(nullable = false, name = "m_desc")
    private String desc;

    //메뉴 금액
    @Column(name = "m_price")
    private String price;

    //메뉴 할인
    @Column(name = "m_discount")
    private String discount;

//    가게고유키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id")
    private Shop shop;

// 디자이너 고유키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    private Designer designer;
}
