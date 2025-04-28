package com.myong.backend.domain.entity.designer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.myong.backend.domain.dto.designer.DesignerWantedDayRequestDto;
import com.myong.backend.domain.entity.shop.Application;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Resume {

    //구직 지원서 고유키
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name ="re_id")
    private UUID id;

    //내용
    @Column(name = "re_content")
    private String content;

    //경력여부
    @Column(name = "re_exp", nullable = false)
    private Exp exp = Exp.Basic;

    //포트폴리오
    @Column(name = "re_portfolio")
    private String portfolio;

    //이미지
    @Column(name = "re_image")
    private String image;

    //해당 이력서의 주인
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    @JsonBackReference
    private Designer designer;

    //이 지원서로 지원한 구인글
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

    // 경력들
    @JsonManagedReference
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Career> careers = new ArrayList<>();

    // 자격증들
    @JsonManagedReference
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Certification> certifications = new ArrayList<>();

    // 희망근무요일
    @JsonManagedReference
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<DesignerWantedDay> wantedDays = new ArrayList<>();

    public Resume(Exp exp, Designer designer, String image) {
        this.exp = exp;
        this.designer = designer;
        this.image = image;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Resume resume = (Resume) o;
        return getId() != null && Objects.equals(getId(), resume.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateExp(Exp exp) {
        this.exp = exp;
    }

    public void updatePortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void connectDesigner(Designer designer) {
        this.designer = designer;
    }


    public void updateWantedDays(List<DesignerWantedDayRequestDto> wantedDays) {
        for (DesignerWantedDayRequestDto wantedDay : wantedDays) {
            DayOfWeek wantedDaysValue = DayOfWeek.valueOf(wantedDay.getWantedDay().toUpperCase());
            DesignerWantedDay designerWantedDay = new DesignerWantedDay(wantedDaysValue);
            this.wantedDays.add(designerWantedDay);
        }
    }
}
