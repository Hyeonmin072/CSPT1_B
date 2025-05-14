package com.myong.backend.domain.entity.chating;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageFile {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    @Column(name = "mf_id")
    private UUID id;        // 아이디

    @Column(name = "mf_fileurl")
    private String fileUrl; // 파일 url

    @ManyToOne
    @JoinColumn(name = "me_id")
    private Message message;
}
