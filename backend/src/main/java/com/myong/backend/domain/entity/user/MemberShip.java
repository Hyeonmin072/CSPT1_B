package com.myong.backend.domain.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MemberShip {

    @Id
    @Column(name = "mbs_id")
    private String id; // 멤버쉽 고유 키

    @OneToOne(fetch = FetchType.LAZY)
    private User user; // 유저 고유 키

    @Column(name = "mbs_grade", nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade; // 등급

    public MemberShip(String id, User user, Grade grade) {
        this.id = id;
        this.user = user;
        this.grade = grade;
    }
}
