package com.myong.backend.domain.entity;

import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "n_id")
    private UUID id;

    @Column(name = "n_content")
    private String content;

    @Column(name = "n_notification_type")
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Notification(String content, NotificationType notificationType, User user) {
        this.content = content;
        this.notificationType = notificationType;
        this.user = user;
    }

}
