package com.myong.backend.domain.entity.business;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "p_id")
    private UUID id; // 결제 고유 키

    @Column(name = "p_price", nullable = false)
    private Long price; // 결제 금액

    @Column(name = "p_reserv_menu_name", nullable = false)
    private String reservMenuName; // 예약한 메뉴명

    // 결제에 reservationId를 저장하되, FK 제약은 걸지 않는다. -> 결제 테이블을 생성하기 전 예약 테이블을 생성 X
    @Column(name = "r_id")
    private UUID reservationId; // 예약 고유 키


    @Column(name = "p_success_yn")
    private Boolean paySuccessYN; // 결제 성공 여부

    @Column(name = "p_fail_reason")
    private String failReason; // 결제 실패 이유

    @Column(name = "p_cancel_yn")
    private Boolean cancelYN; // 결제 취소 여부

    @Column(name = "p_cancel_reason")
    private String cancelReason; // 결제 취소 이유

    @Column(name = "p_payment_key")
    private String paymentKey; // 토스 결제 API 요청을 위한 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id", nullable = false)
    private Designer designer; // 디자이너 고유 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Shop shop; // 가게 고유 키

    @CreatedDate
    @Column(name = "p_pay_date")
    private LocalDateTime createDate; // 결제 날짜

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Payment payment = (Payment) o;
        return getId() != null && Objects.equals(getId(), payment.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    /**
     * 이 결제를 결제성공 상태로 업데이트하는 편의 메서드
     * @param paymentKey 토스 결제 API 요청을 위한 고유 키
     * @param reservationId 이 결제가 해당되는 예약 객체의 고유 키
     */
    public void successUpdate(String paymentKey, UUID reservationId) {
        this.paymentKey = paymentKey;
        this.reservationId = reservationId;
        this.paySuccessYN = true;
    }


    /**
     * 이 결제를 결제실패 상태로 업데이트하는 편의 메서드
     * @param failReason 실패 이유를 담은 메시지
     */
    public void failUpdate(String failReason) {
        this.failReason = failReason;
        this.paySuccessYN = false;
    }


    /**
     * 이 결제를 결제취소 상태로 업데이트하는 편의 메서드
     * @param cancelReason 취소 이유를 담은 메시지
     */
    public void cancelUpdate(String cancelReason) {
        this.cancelReason = cancelReason;
        this.reservationId = null;
        this.cancelYN = true;
    }
}
