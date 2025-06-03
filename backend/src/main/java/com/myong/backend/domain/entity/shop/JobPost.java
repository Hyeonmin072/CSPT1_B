package com.myong.backend.domain.entity.shop;

import com.myong.backend.domain.dto.job.JobPostRequestDto;
import com.myong.backend.domain.entity.Gender;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class JobPost {

    //구인 게시물 고유키
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "jp_id")
    private UUID id;

    //제목
    @Column(name = "jp_title", nullable = false)
    private String title;

    //내용
    @Column(name = "jp_content", nullable = false)
    private String content;

    // 근무 형태
    @Column(name = "jp_work", nullable = false)
    @Enumerated(EnumType.STRING)
    private Work work = Work.FULLTIME; //정규직 디폴트

    //급여
    @Column(name = "jp_salary")
    private String salary = "";

    //요구 성별
    @Column(name = "jp_gender")
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.NO;

    //요구 정시출근시간
    @Column(name = "jp_worktime")
    private LocalTime workTime = LocalTime.of(0,0); // 00:00

    //요구 정시퇴근시간
    @Column(name = "jp_leavetime")
    private LocalTime leaveTime = LocalTime.of(0,0); // 00:00

    //첨부파일
    @Column(name = "jp_file")
    private String file = "";

    //생성일
    @CreatedDate
    @Column(name = "jp_create_date")
    private LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    @OneToMany(mappedBy = "jobPost",cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>(); // 이 구인글에 지원된 지원서들


    @Builder
    public JobPost(String file, LocalTime leaveTime, LocalTime workTime, Gender gender, String salary, Work work, String content, String title, Shop shop) {
        this.file = file;
        this.leaveTime = leaveTime;
        this.workTime = workTime;
        this.gender = gender;
        this.salary = salary;
        this.work = work;
        this.content = content;
        this.title = title;
        this.shop = shop;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        JobPost jobPost = (JobPost) o;
        return getId() != null && Objects.equals(getId(), jobPost.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void updateJobPost(JobPostRequestDto request) {
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            this.title = request.getTitle();
        }
        if (request.getSalary() != null && !request.getSalary().isBlank()) {
            this.salary = request.getSalary();
        }
        if (request.getGender() != null) {
            try {
                this.gender = Gender.valueOf(request.getGender()); // Enum 변환 시 예외 처리
            } catch (IllegalArgumentException e) {
                System.err.println("잘못된 Gender 값: " + request.getGender());
            }
        }
        if (request.getWork() != null) {
            try {
                this.work = Work.valueOf(request.getWork());
            } catch (IllegalArgumentException e) {
                System.err.println("잘못된 Work 값: " + request.getWork());
            }
        }
        if (request.getWorkTime() != null) {
            this.workTime = LocalTime.parse(request.getWorkTime().toString(), DateTimeFormatter.ofPattern("HH:mm"));
        }
        if (request.getLeaveTime() != null) {
            this.leaveTime = LocalTime.parse(request.getLeaveTime().toString(), DateTimeFormatter.ofPattern("HH:mm"));
        }

        if (request.getContent() != null && !request.getContent().isBlank()) {
            this.content = request.getContent();
        }
    }


}
