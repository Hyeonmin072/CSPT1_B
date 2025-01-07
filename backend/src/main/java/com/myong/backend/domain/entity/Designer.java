package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Designer {

    @Id
    @Column(name = "d_id")
    private String id = UUID.randomUUID().toString(); // 고유 키


    @Column(name = "d_name", nullable = false)
    private String name; // 이름

    @Column(name = "d_desc")
    private String desc; // 소개글

    @Column(name = "d_email", nullable = false)
    private String email; // 이메일

    @Column(name = "d_pwd", nullable = false)
    private String pwd; // 비밀번호

    @Column(name = "d_tel", nullable = false)
    private String tel; // 연락처

    @Column(name = "d_createdate", nullable = false, updatable = false)
    private LocalDateTime createDate = LocalDateTime.now(); // 가입일

    @Column(name = "d_image")
    private String image; // 사진

    @Column(name = "d_post", nullable = false)
    private Long post; // 우편번호

    @Column(name = "d_exp", nullable = false)
    private Long exp; // 경력

    @Column(name = "d_edu")
    private Long edu; // 학력

    @Column(name = "d_certification")
    private Long certification; // 자격증
 
    @Column(name = "d_birth", nullable = false)
    private Long birth; // 생년월일

    @Column(name = "d_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender; //성별

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id")
    private Shop shop;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designer")
    private List<Attendance> attendance = new ArrayList<>();

}
