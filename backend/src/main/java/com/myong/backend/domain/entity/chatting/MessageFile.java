package com.myong.backend.domain.entity.chatting;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MessageFile {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID)
    @Column(name = "mf_id")
    private UUID id;        // 아이디

    @Column(name = "mf_fileurl")
    private String fileUrl; // 파일 url

    @ManyToOne
    @JoinColumn(name = "me_id")
    private Message message;

    public static MessageFile save(String url, Message message){
        return MessageFile.builder()
                .fileUrl(url)
                .message(message)
                .build();
    }
}
