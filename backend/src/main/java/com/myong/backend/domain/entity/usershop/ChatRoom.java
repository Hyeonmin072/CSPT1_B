package com.myong.backend.domain.entity.usershop;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @Column(name = "cr_id")
    private UUID id = UUID.randomUUID(); // 채팅방 고유 키

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
