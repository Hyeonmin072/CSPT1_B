package com.myong.backend.domain.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class MemberShip {

    @Id
    @Column(name = "mbs_id")
    private UUID id = UUID.randomUUID(); // 멤버쉽 고유 키

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    @Column(name = "mbs_grade", nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade; // 등급

    public MemberShip(User user, Grade grade) {
        this.user = user;
        this.grade = grade;
    }
}
