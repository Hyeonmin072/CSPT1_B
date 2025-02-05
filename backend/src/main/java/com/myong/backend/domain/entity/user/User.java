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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @Column(name = "u_id",nullable = false)
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

    @Column(nullable = false, name = "u_birth_date")
    private LocalDate birthDate = LocalDate.now(); // 생년월일

    @Column(nullable = false, name = "u_gender")
    @Enumerated(EnumType.STRING)
    private Gender gender; // 성별

    @Column(nullable = false, name = "u_address")
    private String address; // 거주지

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>(); // 유저권한

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserCoupon> coupons = new ArrayList<>(); // 소유한 쿠폰들
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserShop> shops = new ArrayList<>(); // 예약한 샵들


    public User(String name, String email, String pwd, String tel, LocalDate birthDate, Gender gender, String address) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.pwd = pwd;
        this.tel = tel;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
