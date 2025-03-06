package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.dto.shop.ShopProfileRequestDto;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.usershop.Review;
import com.myong.backend.domain.entity.usershop.UserShop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private Double rating = 0.0; // 총 평점

    @Column(name = "s_total_rating")
    private Double totalRating = 0.0;   // 리뷰 평점 합계

    @Column(name = "s_review_count")
    private Integer reviewCount = 0;   // 리뷰 개수

    @Column(name = "s_desc")
    private String desc = ""; // 소개

    @Column(name = "s_biz_id", nullable = false)
    private String bizId; // 사업자번호

    @Column(name = "s_pwd", nullable = false)
    private String pwd; // 비밀번호

    @Column(name = "s_longitude", nullable = false)
    private Double longitude; // 경도

    @Column(name = "s_latitude", nullable = false)
    private Double latitude; // 위도

    @Column(name = "s_open_time")
    private LocalTime openTime = LocalTime.of(0,0); // 오픈시간 00:00

    @Column(name = "s_close_time")
    private LocalTime closeTime = LocalTime.of(0,0); // 마감시간 00:00

    @Column(name = "s_post", nullable = false)
    private Integer post; // 우편번호

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>(); // 가게에 대한 리뷰들

    @OneToMany(mappedBy = "shop", cascade =  CascadeType.ALL)
    private List<Designer> designers = new ArrayList<>(); // 소속 디자이너들

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<UserShop> users = new ArrayList<>(); // 예약한 손님들

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<ShopHoliday> holidays = new ArrayList<>(); // 휴무일(LocalDate)

    @Column(name = "s_regular_holiday")
    private String regularHoliday = ""; // 정기 휴무일

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Coupon> coupons = new ArrayList<>(); // 등록한 쿠폰들

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Event> events = new ArrayList<>(); // 등록한 이벤트들
    

    public Shop(String name, String pwd, String email, String address, String tel, String bizId, Integer post) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.tel = tel;
        this.bizId = bizId;
        this.pwd = pwd;
        this.post = post;
    }
    public void updateRating(Double rating){
        this.rating=rating;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Shop shop = (Shop) o;
        return getId() != null && Objects.equals(getId(), shop.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void updateProfile(ShopProfileRequestDto request) {
        if (!request.getName().equals(this.name)) { // 이름
            this.name = request.getName();
        }
        if (!request.getAddress().equals(this.address)) { // 주소
            this.address = request.getAddress();
        }
        if (!request.getPost().equals(this.post)) { // 우편번호
            this.post = request.getPost();
        }
        if (!request.getTel().equals(this.tel)) { // 전화번호
            this.tel = request.getTel();
        }
        if (request.getNewPwd().equals(request.getNewPwdConfirm()) && !request.getNewPwd().isBlank()) { // 비밀번호
            this.pwd = request.getNewPwd();
        }
        if (!request.getDesc().equals(this.desc) && !request.getDesc().isBlank()) { // 설명
            this.desc = request.getDesc();
        }
        if (!request.getOpen().equals(this.openTime.toString()) && !request.getOpen().isBlank()) { // 오픈시간
            this.openTime = LocalTime.parse(request.getOpen(), DateTimeFormatter.ofPattern("HH:mm"));
        }
        if (!request.getClose().equals(this.closeTime.toString()) && !request.getClose().isBlank()) { // 마감시간
            this.closeTime =  LocalTime.parse(request.getClose(), DateTimeFormatter.ofPattern("HH:mm"));
        }
        if (!request.getRegularHoliday().equals(this.regularHoliday) && !request.getRegularHoliday().isBlank()) { // 정기 휴무일
            this.regularHoliday = request.getRegularHoliday();
        }
    }
}
