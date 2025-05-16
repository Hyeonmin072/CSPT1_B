package com.myong.backend.domain.entity.user;

import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.chatting.Message;
import com.myong.backend.domain.entity.userdesigner.UserDesignerLike;
import com.myong.backend.domain.entity.usershop.UserShop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Builder(toBuilder = true)
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "u_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id ; // 유저 고유 키

    @Column(name = "u_name",  nullable = false)
    private String name; // 이름

    @Column(name = "u_email", nullable = false)
    private String email; // 이메일

    @Column(name = "u_pwd")
    private String pwd; // 비밀번호  * 소셜 로그인을 위한 비밀번호널값 허용 *

    @Column(name = "u_tel", nullable = false)
    private String tel; // 연락처

    @Column(name = "u_location",nullable = false)
    private String location; // 위치 * 매번 설정하는 유저위치 *

    @Column(name = "u_birth_date", nullable = false)
    private LocalDate birthDate = LocalDate.now(); // 생년월일

    @Column(name = "u_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender; // 성별

    @Column(name = "u_address", nullable = false)
    private String address; // 거주지

    @Column(name = "u_post", nullable = false)
    private Integer post; // 우편번호

    @Column(name = "u_longitude" ,nullable = false)
    private Double longitude;  // x : 경도

    @Column(name = "u_latitude",nullable = false)
    private Double latitude;    // y : 위도

    @Column(name = "u_signin_type",nullable = false)
    private SigninType signinType = SigninType.NONE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();  // 자기가 쓴 메세지들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserCoupon> coupons = new ArrayList<>(); // 소유한 쿠폰들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserShop> shops = new ArrayList<>(); // 예약한 샵들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserDesignerLike> userDesignerLikes = new ArrayList<>(); // 유저가 좋아요 누른 디자이너

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MemberShip memberShip;   // 유저의 맴버쉽 등급



    public User(String name, String email, String pwd, String tel, LocalDate birthDate, Gender gender, String address, Integer post ,Double longitude, Double latitude,String location) {
        this.name = name;
        this.email = email;
        this.pwd = pwd;
        this.tel = tel;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.post = post;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
    }


}
