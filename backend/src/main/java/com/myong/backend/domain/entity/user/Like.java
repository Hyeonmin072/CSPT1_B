package com.myong.backend.domain.entity.user;

import com.myong.backend.domain.entity.designer.Designer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
public class Like {

    @EmbeddedId
    private LikeId id; // 좋아요 아이디 

    @JoinColumn(nullable = false, name = "u_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 유저 고유 키

    @JoinColumn(nullable = false, name = "d_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Designer designer; // 디자이너 고유 키

    public Like(User user, Designer designer) {
        this.id = new LikeId(user.getId(), designer.getId());
        this.user = user;
        this.designer = designer;
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    /**
     * 복합키(직렬화 사용)
     */
    private static class LikeId implements Serializable {
        private String userId;
        private String designerId;
    }
}
