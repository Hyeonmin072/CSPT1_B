package com.myong.backend.domain.entity.designer;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
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
    private UUID id = UUID.randomUUID(); // 고유 키

    @Column(name = "d_name", nullable = false)
    private String name; // 이름

    @Column(name = "d_nickname", nullable = false)
    private String nickName;  //닉네임

    @Column(name = "d_desc")
    private String desc; // 소개글

    @Column(name = "d_email", nullable = false)
    private String email; // 이메일

    @Column(name = "d_pwd", nullable = false)
    private String pwd; // 비밀번호

    @Column(name = "d_tel", nullable = false)
    private String tel; // 연락처

    @Column(name = "d_image")
    private String image; // 사진

    @Column(name = "d_birth_date", nullable = false)
    private LocalDate birth; // 생년월일

    @Column(name = "d_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender; //성별

    @Column(name = "d_like")
    private Long like = 0L; //좋아요

    @Column(name = "d_rating" , nullable = false)
    private Double rating = 0.0; // 평점 총 합계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id")
    @JsonManagedReference
    private Shop shop;   // 소속된 가게

    @OneToOne(mappedBy = "designer", cascade = CascadeType.ALL)
    private Resume resume;

    @OneToMany(mappedBy = "designer", cascade = CascadeType.ALL)
    private List<DesignerHoliday> holidays = new ArrayList<>();  // 휴무일(LocalDate)

    @OneToMany(mappedBy = "designer", cascade = CascadeType.ALL)
    private List<DesignerRegularHoliday> regularHolidays = new ArrayList<>(); //정기 휴무일(DayOfWeek)

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designer")
    private List<Attendance> attendance = new ArrayList<>();

    public Designer(String name, String nickName, String email, String pwd, String tel, LocalDate birth, Gender gender) {
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.pwd = pwd;
        this.tel = tel;
        this.birth = birth;
        this.gender = gender;
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

    public void updatePwd(String newPwd) {
        this.pwd = newPwd;
    }

    public void updateTel(String tel) {
        this.tel = tel;
    }

    public void updateImage(String image) {
        this.image = image;
    }

}
