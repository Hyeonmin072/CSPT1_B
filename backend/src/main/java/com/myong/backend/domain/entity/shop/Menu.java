package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.dto.menu.MenuRequestDto;
import com.myong.backend.domain.entity.designer.Designer;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Builder;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "m_id")
    private UUID id; // 메뉴 고유 키

    @Column(name = "m_name", nullable = false)
    private String name; // 이름

    @Column(name = "m_desc", nullable = false)
    private String desc; // 설명

    @Column(name = "m_price")
    private Integer price = 0; // 금액

    @Column(name = "m_image")
    private String image = ""; // 메뉴이미지

    @Column(name = "m_estimated_time")
    private String estimatedTime = ""; // 소요시간

    @Column(name = "m_common", nullable = false)
    private String common; // 공통여부

    @Column(name = "m_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private MenuCategory category = MenuCategory.NONE; // 메뉴 카테고리

    @Column(name = "m_recommend")
    private boolean recommend = false; // 추천 메뉴 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false )
    private Shop shop; // 가게 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false )
    private Designer designer; // 디자이너 고유 키

    @OneToOne
    @JoinColumn(name = "e_id", nullable = true)
    private Event event;

    @Builder
    public Menu(String name, String desc, Integer price, String estimatedTime, String common, Shop shop, Designer designer, MenuCategory category) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.common = common;
        this.shop = shop;
        this.designer = designer;
        this.category = category;
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

    public void edit(@Valid MenuRequestDto request) {
        if (!request.getName().equals(this.name)) { // 이름
            this.name = request.getName();
        }
        if (!request.getDesc().equals(this.desc)) { // 설명
            this.desc = request.getDesc();
        }
        if (!request.getCommon().equals(this.common)) { // 공통여부
            this.common = request.getCommon();
        }
        if (!request.getPrice().equals(this.price) && request.getPrice() > 0) { // 금액
            this.price = request.getPrice();
        }
        if (!request.getEstimatedTime().equals(this.estimatedTime) && !request.getEstimatedTime().isBlank()) { // 소요시간
            this.estimatedTime = request.getEstimatedTime();
        }
        if (!request.getCategory().equals(this.category) && request.getCategory() != null) { // 소요시간
            this.category = request.getCategory();
        }
    }
}
