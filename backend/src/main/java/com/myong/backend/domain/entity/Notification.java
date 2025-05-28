package com.myong.backend.domain.entity;

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

    @Column(name = "n_receiver_email")
    private String receiverEmail;

    @Builder
    public Notification(String content, NotificationType notificationType, String receiverEmail) {
        this.content = content;
        this.notificationType = notificationType;
        this.receiverEmail = receiverEmail;
    }
}
