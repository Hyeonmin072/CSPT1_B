package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.dto.shop.ShopNoticeRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "n_id")
    private UUID id; // 공지사항 아이디

    @Column(name = "n_title", nullable = false)
    private String title; // 공지사항 제목

    @Column(name = "n_content")
    private String content; // 공지사항 내용

    @Column(name = "n_importance")
    private Boolean importance; // 공지사항 중요여부, true = 중요, false = 비중요

    @CreatedDate
    @Column(name = "n_create_date")
    private LocalDateTime createDate; // 공지사항 생성일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "S_id")
    private Shop shop; // 공지사항이 소속된 가게

    @Builder
    public Notice(String content, String title, Shop shop, Boolean importance) {
        this.content = content;
        this.title = title;
        this.shop = shop;
        this.importance = importance;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Notice notice = (Notice) o;
        return getId() != null && Objects.equals(getId(), notice.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }


    /**
     * 수정 편의 메서드
     * @param request 공지사항 개체 수정에 필요한 정보가 담긴 DTO
     */
    public void update(ShopNoticeRequestDto request) {
        if(!this.getTitle().equals(request.getTitle())) { // 현재 공지사항의 제목과 DTO의 제목이 동일하지 않은 경우
            this.title = request.getTitle();
        } 
        if(!this.getContent().equals(request.getContent())) { // 현재 공지사항의 내용과 DTO의 내용이 동일하지 않은 경우
            this.content = request.getContent();
        }
        if (!this.getImportance().equals(request.getImportance())) {
            this.importance = request.getImportance();
        }
    }
}
