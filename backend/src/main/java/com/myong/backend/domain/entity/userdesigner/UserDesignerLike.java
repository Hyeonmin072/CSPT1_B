package com.myong.backend.domain.entity.userdesigner;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserDesignerLike {

    @EmbeddedId
    private UserDesignerLikeId id; // 좋아요 아이디

    @JoinColumn(name = "u_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 유저 고유 키

    @JoinColumn(name = "d_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Designer designer; // 디자이너 고유 키

    @CreatedDate
    private LocalDate createdDate; // 유저가 디자이너를 좋아요 한 날짜

    @Builder
    public UserDesignerLike(UserDesignerLikeId id, User user, Designer designer) {
        this.id = id;
        this.user = user;
        this.designer = designer;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserDesignerLike userDesignerLike = (UserDesignerLike) o;
        return getId() != null && Objects.equals(getId(), userDesignerLike.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Builder
    /**
     * 복합키(직렬화 사용)
     */
    public static class UserDesignerLikeId implements Serializable {
        private UUID userId;
        private UUID designerId;
    }
}
