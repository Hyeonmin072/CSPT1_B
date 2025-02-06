package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.usershop.UserShop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Shop {

    @Id
    @Column(name = "s_id")
    private UUID id = UUID.randomUUID(); // 가게 고유 키

    @Column(name = "s_name", nullable = false)
    private String name; // 이름
    
    @Column(name = "s_email", nullable = false)
    private String email; // 이메일

    @Column(name = "s_address", nullable = false)
    private String address; // 상세주소

    @Column(name = "s_tel", nullable = false)
    private String tel; // 연락처

    @Column(name = "s_rating", nullable = false)
    private Double rating = 0.0; // 평점

    @Column(name = "s_desc")
    private String desc; // 소개

    @Column(name = "s_biz_id", nullable = false)
    private String bizId; // 사업자번호

    @Column(name = "s_pwd", nullable = false)
    private String pwd; // 비밀번호

    @Column(name = "s_longitude")
    private Double longitude; // 경도

    @Column(name = "s_latitude")
    private Double latitude; // 위도

    @Column(name = "s_open_time")
    private LocalTime openTime; // 오픈시간

    @Column(name = "s_close_time")
    private LocalTime closeTime; // 마감시간

    @Column(name = "s_post", nullable = false)
    private Integer post; // 우편번호

    @OneToMany(mappedBy = "shop", cascade =  CascadeType.ALL)
    private List<Designer> designers = new ArrayList<>(); // 소속 디자이너들

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<UserShop> users = new ArrayList<>(); // 예약한 손님들

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<ShopHoliday> holidays = new ArrayList<>(); // 휴무일(LocalDate)

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<ShopRegularHoliday> regularHolidays = new ArrayList<>(); // 정기 휴무일(DayOfWeek)
    

    public Shop(String name, String pwd, String address, String tel, String bizId, Integer post) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.bizId = bizId;
        this.pwd = pwd;
        this.post = post;
    }
}
