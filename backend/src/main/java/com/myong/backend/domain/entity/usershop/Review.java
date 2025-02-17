package com.myong.backend.domain.entity.usershop;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @Column(name = "rv_id")
    private UUID id = UUID.randomUUID(); // 리뷰 고유 키

    @Column(name = "rv_content", nullable = false)
    private String content; // 내용

    @Column(name = "rv_rating", nullable = false)
    private Double rating; // 평점

    @Column(name = "rv_image")
    private String image; // 이미지 경로

    @CreatedDate
    @Column(name = "rv_create_date", updatable = false)
    private LocalDateTime createDate; // 작성일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "r_id", nullable = false)
    private Reservation reservation; // 예약 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer; // 디자이너 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rva_id")
    private ReviewAnswer reviewAnswer; // 리뷰 답변 고유 키

    public Review(String content, Double rating, Reservation reservation, Shop shop, Designer designer, User user) {
        this.content = content;
        this.rating = rating;
        this.reservation = reservation;
        this.shop = shop;
        this.designer = designer;
        this.user = user;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Review review = (Review) o;
        return getId() != null && Objects.equals(getId(), review.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
