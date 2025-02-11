package com.myong.backend.domain.entity.usershop;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

    @Id
    @Column(name = "cr_id")
    private UUID id = UUID.randomUUID(); // 채팅방 고유 키

    @CreatedDate
    @Column(name = "cr_create_date", updatable = false)
    private LocalDateTime createDate; // 채팅방 생성일

    @Column(name = "cr_last_message", nullable = false)
    private String lastMessage = ""; // 채팅방 마지막 메시지

    @ManyToOne
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer; // 디자이너 고유 키

    @ManyToOne
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages; // 채팅방의 메시지들


    public ChatRoom(Designer designer, User user) {
        this.designer = designer;
        this.user = user;
    }


}
