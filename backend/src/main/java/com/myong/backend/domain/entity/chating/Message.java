package com.myong.backend.domain.entity.chating;

import com.myong.backend.domain.dto.chating.request.ChatMessageRequestDto;
import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "me_id")
    private UUID id; // 메시지 고유 키

    @Column(name = "me_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType; // 타입

    @Column(name = "me_content", nullable = false)
    private String content; // 내용

    @CreatedDate
    @Column(name = "me_send_date", updatable = false)
    private LocalDateTime sendDate; // 전송 시간

    @Column(name = "me_sender", nullable = false)
    private String sender; // 보낸 사람 이름

    @Column(name = "me_read")
    private boolean read; // 읽은 여부 판단

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cr_id", nullable = false)
    private ChatRoom chatRoom; // 채팅방 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id",nullable = false)
    private User user;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageFile> files = new ArrayList<>();    // 파일 리스트

    public Message(MessageType messageType, String content, String sender, ChatRoom chatRoom) {
        this.messageType = messageType;
        this.content = content;
        this.sender = sender;
        this.chatRoom = chatRoom;
    }
    public static Message saveMessage(ChatMessageRequestDto request, String sender, ChatRoom chatRoom){
        return Message.builder()
                .messageType(MessageType.TEXT)
                .content(request.content())
                .sendDate(request.sendDate())
                .sender(sender)
                .read(false)
                .chatRoom(chatRoom)
                .build();
    }

    public static Message saveFileMessage(ChatMessageRequestDto request, String sender, ChatRoom chatRoom, MessageType messageType){
        return Message.builder()
                .messageType(messageType)
                .content(request.content())
                .sendDate(request.sendDate())
                .sender(sender)
                .read(false)
                .chatRoom(chatRoom)
                .build();
    }

    public void markAsRead(){
        this.read = true;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Message message = (Message) o;
        return getId() != null && Objects.equals(getId(), message.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
