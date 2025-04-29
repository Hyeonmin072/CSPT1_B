package com.myong.backend.domain.entity.designer;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.business.Payment;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.shop.Menu;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.userdesigner.UserDesignerLike;
import com.myong.backend.domain.entity.usershop.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Designer {

    @Id
    @Column(name = "d_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // 고유 키

    @Column(name = "d_name", nullable = false)
    private String name; // 이름

    @Column(name = "d_nickname", nullable = false)
    private String nickName;  //닉네임

    @Column(name = "d_desc")
    private String desc; // 소개글

    @Column(name = "d_email", nullable = false)
    private String email; // 이메일

    @Column(name = "d_pwd", nullable = false)
    private String password; // 비밀번호

    @Column(name = "d_tel", nullable = false)
    private String tel; // 연락처

    @Column(name = "d_image")
    private String image; // 사진

    @Column(name = "d_back_image")
    private String backgroundImage; // 디자이너 배경 사진

    @Column(name = "d_birth_date", nullable = false)
    private LocalDate birth; // 생년월일

    @Column(name = "d_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender; //성별

    @Column(name = "d_like")
    private Integer like = 0; //좋아요

    @Column(name = "d_rating" , nullable = false)
    private Double rating = 0.0; // 총 평점

    @Column(name = "d_total_rating")
    private Double totalRating = 0.0; // 평점 합계

    @Column(name = "d_review_count")
    private Integer reviewCount = 0;  // 리뷰 개수

    @Column(name = "d_work_time", nullable = false)
    private LocalTime workTime = LocalTime.of(0,0);  // 출근 시간(가게 소속일때만 활성화)

    @Column(name = "d_leave_time", nullable = false)
    private LocalTime leaveTime = LocalTime.of(0,0);  // 출근 시간(가게 소속일때만 활성화)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id")
    @JsonManagedReference
    private Shop shop;   // 소속된 가게

    @OneToOne(mappedBy = "designer", cascade = CascadeType.ALL)
    private Resume resume;//이력서

    @OneToMany(mappedBy = "designer", cascade = CascadeType.ALL)
    private List<DesignerHoliday> holidays = new ArrayList<>();  // 휴무일(LocalDate)

    @OneToMany(mappedBy = "designer", cascade = CascadeType.ALL)
    private List<DesignerRegularHoliday> regularHolidays = new ArrayList<>(); //정기 휴무일(DayOfWeek)

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designer")
    private List<Attendance> attendance = new ArrayList<>(); // 출결

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designer")
    private List<Review> reviews = new ArrayList<>(); // 리뷰

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designer")
    private List<Menu> menus = new ArrayList<>(); // 메뉴

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designer")
    private List<Reservation> reservations = new ArrayList<>(); // 예약

    @OneToMany(mappedBy = "designer", cascade = CascadeType.ALL)
    private List<UserDesignerLike> userDesignerLikes = new ArrayList<>(); // 디자이너를 좋아요한 유저들

    @OneToMany(mappedBy = "designer", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>(); // 디자이너의 결제들


    public Designer(String name, String nickName, String email, String password, String tel, LocalDate birth, Gender gender) {
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.tel = tel;
        this.birth = birth;
        this.gender = gender;
    }

    public void updateRating(Double rating,Double totalRating,Integer reviewCount){
        this.rating = rating;
        this.totalRating = totalRating;
        this.reviewCount = reviewCount;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Designer designer = (Designer) o;
        return getId() != null && Objects.equals(getId(), designer.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    // 프로필 수정 메소드들
    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateDesc(String desc) {
        this.desc = desc;
    }

    public void updatePwd(String newPassword) {
        this.password = newPassword;
    }

    public void updateTel(String tel) {
        this.tel = tel;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void getJob(Shop shop) {
        this.shop = shop;
    }

    public void updateBackgroundImage(String backgroundImage) {this.backgroundImage = backgroundImage;}

    public void fire() {
        this.shop = null;
        this.workTime = LocalTime.of(0, 0);
        this.leaveTime = LocalTime.of(0, 0);
    }

    public void updateWorkAndLeave(LocalTime workTime, LocalTime leaveTime) {
        if(!this.workTime.equals(workTime)) this.workTime = workTime;
        if(!this.leaveTime.equals(leaveTime)) this.leaveTime = leaveTime;
    }
}
