package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.user.DiscountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Menu {

    @Id
    @Column(name = "m_id")
    private UUID id = UUID.randomUUID(); // 메뉴 고유 키

    @Column(name = "m_name", nullable = false)
    private String name; // 이름

    @Column(name = "m_desc", nullable = false)
    private String desc; // 설명

    @Column(name = "m_price")
    private Long price; // 금액

    @Column(name = "m_discount")
    private String discount; // 할인

    @Column(name = "m_discount_type")
    private DiscountType discountType; // 할인방식

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false )
    private Shop shop; // 가게 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false )
    private Designer designer; // 디자이너 고유 키


    public Menu(String name, String desc, Shop shop, Designer designer) {
        this.name = name;
        this.desc = desc;
        this.shop = shop;
        this.designer = designer;
    }
}
