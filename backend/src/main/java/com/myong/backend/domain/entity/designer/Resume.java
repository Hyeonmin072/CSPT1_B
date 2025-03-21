package com.myong.backend.domain.entity.designer;

import com.myong.backend.domain.entity.shop.Application;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

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
    @Column(name ="re_id")
    private UUID id = UUID.randomUUID();

    //내용
    @Column(name = "re_content")
    private String content;

    //경력여부
    @Column(name = "re_exp", nullable = false)
    private String exp;

    //포트폴리오
    @Column(name = "re_portfolio")
    private String portfolio;

    //해당 이력서의 주인
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer;

    //이 지원서로 지원한 구인글
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

    // 경력들
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Career> careers = new ArrayList<>();

    // 자격증들
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Certification> certifications = new ArrayList<>();

    // 희망근무요일
    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<DesignerWantedDay> wantedDays = new ArrayList<>();

    public Resume(String exp, Designer designer) {
        this.exp = exp;
        this.designer = designer;
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
}
