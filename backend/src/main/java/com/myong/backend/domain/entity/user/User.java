package com.myong.backend.domain.entity.user;

import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.usershop.UserShop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "u_id")
    private UUID id = UUID.randomUUID(); // 유저 고유 키

    @Column(name = "u_name",  nullable = false)
    private String name; // 이름

    @Column(name = "u_email", nullable = false)
    private String email; // 이메일

    @Column(name = "u_pwd", nullable = false)
    private String pwd; // 비밀번호

    @Column(name = "u_tel", nullable = false)
    private String tel; // 연락처

    @Column(name = "u_location")
    private String location; // 위치

    @Column(name = "u_birth_date", nullable = false)
    private LocalDate birthDate = LocalDate.now(); // 생년월일

    @Column(name = "u_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender; // 성별

    @Column(name = "u_address", nullable = false)
    private String address; // 거주지

    @Column(name = "u_longitude")
    private String longitude;  // x : 경도

    @Column(name = "u_latitude")
    private String latitude;    // y : 위도

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserCoupon> coupons = new ArrayList<>(); // 소유한 쿠폰들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserShop> shops = new ArrayList<>(); // 예약한 샵들


    public User(String name, String email, String pwd, String tel, LocalDate birthDate, Gender gender, String address) {
        this.name = name;
        this.email = email;
        this.pwd = pwd;
        this.tel = tel;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;

    }


}
