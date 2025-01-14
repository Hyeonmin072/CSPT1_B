package com.myong.backend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "u_id")
    private String id; // 유저 고유 키

    @Column(nullable = false,  name = "u_name")
    private String name; // 이름

    @Column(nullable = false, name = "u_email")
    private String email; // 이메일

    @Column(nullable = false, name = "u_pwd")
    private String pwd; // 비밀번호

    @Column(nullable = false, name = "u_tel")
    private String tel; // 연락처

    @Column(name = "u_location")
    private String location; // 위치

    @Column(nullable = false, name = "u_create_date", updatable = false)
    private LocalDateTime createDate = LocalDateTime.now(); // 가입일

    @Column(nullable = false, name = "u_birth_date")
    private LocalDateTime birthDate = LocalDateTime.now(); // 생년월일

    @Column(nullable = false, name = "u_gender")
    @Enumerated(EnumType.STRING)
    private Gender gender; // 성별

    public User(String name, String email, String pwd, String tel, LocalDateTime createDate, LocalDateTime birthDate, Gender gender) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.pwd = pwd;
        this.tel = tel;
        this.createDate = createDate;
        this.birthDate = birthDate;
        this.gender = gender;
    }
}
