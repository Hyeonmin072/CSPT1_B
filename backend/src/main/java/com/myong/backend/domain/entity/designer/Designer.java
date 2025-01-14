package com.myong.backend.domain.entity.designer;

import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Designer {

    @Id
    @Column(name = "d_id")
    private String id; // 디자이너 고유 키

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

    @Column(name = "d_create_date", nullable = false, updatable = false)
    private LocalDateTime createDate; // 가입일

    @Column(name = "d_image")
    private String image; // 사진

    @Column(name = "d_post", nullable = false)
    private Long post; // 우편번호

    @Column(name = "d_exp", nullable = false)
    private Long exp; // 경력

    @Column(name = "d_edu")
    private String edu; // 학력

    @Column(name = "d_certification")
    private String certification; // 자격증
 
    @Column(name = "d_birth_day", nullable = false)
    private LocalDate birthDay; // 생년월일

    @Column(name = "d_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender; //성별

    @Column(name="d_like", nullable = false)
    private Long like; // 좋아요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id")
    private Shop shop; // 가게 고유 키

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designer")
    private List<Attendance> attendances = new ArrayList<>();

    public Designer(String name, String email, String pwd, String tel, Long post, Long exp, LocalDate birthDay, Gender gender) {
        this.name = name;
        this.email = email;
        this.pwd = pwd;
        this.tel = tel;
        this.createDate = LocalDateTime.now();
        this.post = post;
        this.exp = exp;
        this.birthDay = birthDay;
        this.gender = gender;
        this.like = 0L;
    }
}
