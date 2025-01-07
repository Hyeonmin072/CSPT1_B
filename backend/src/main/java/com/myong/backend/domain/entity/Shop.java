package com.myong.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Entity
@Getter
public class Shop {

    @Id
    @Column(name = "s_id")
    private String id = UUID.randomUUID().toString(); // 가게 고유 키

    @Column(name = "s_name", nullable = false)
    private String name; //이름

    @Column(name = "s_pwd", nullable = false)
    private String pwd; //비밀번호

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

    @Column(name = "s_longitude")
    private Double longitude; // 경도

    @Column(name = "s_latitude")
    private Double latitude; // 위도

    @Column(name = "s_opentime")
    private String openTime; // 오픈시간

    @Column(name = "s_closetime")
    private String closeTime; // 마감시간

    @Column(name = "s_close_day")
    private Double closeDay; // 휴무일

    @Column(name = "s_post", nullable = false)
    private Long post; // 우편번호

    @Column(name = "s_category")
    private Long category; // 카테고리코드

    @OneToMany(mappedBy = "shop")
    private List<Designer> designer = new ArrayList<>();

}
