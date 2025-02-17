package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.entity.designer.Designer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
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

    @Column(name = "m_estimated_time")
    private String estimatedTime; // 소요시간

    @Column(name = "m_common", nullable = false)
    private Boolean common; // 공통여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false )
    private Shop shop; // 가게 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false )
    private Designer designer; // 디자이너 고유 키


    public Menu(String name, String desc, Boolean common, Shop shop, Designer designer) {
        this.name = name;
        this.desc = desc;
        this.common = common;
        this.shop = shop;
        this.designer = designer;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Menu menu = (Menu) o;
        return getId() != null && Objects.equals(getId(), menu.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
