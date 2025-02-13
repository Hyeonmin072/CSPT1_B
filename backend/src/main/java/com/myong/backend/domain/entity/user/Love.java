package com.myong.backend.domain.entity.user;

import com.myong.backend.domain.entity.designer.Designer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Love {

    @EmbeddedId
    private LoveId id; // 좋아요 아이디

    @JoinColumn(name = "u_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 유저 고유 키

    @JoinColumn(name = "d_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Designer designer; // 디자이너 고유 키

    public Love(User user, Designer designer) {
        this.id = new LoveId(user.getId(), designer.getId());
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
        Love love = (Love) o;
        return getId() != null && Objects.equals(getId(), love.getId());
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
    /**
     * 복합키(직렬화 사용)
     */
    private static class LoveId implements Serializable {
        private UUID userId;
        private UUID designerId;
    }
}
