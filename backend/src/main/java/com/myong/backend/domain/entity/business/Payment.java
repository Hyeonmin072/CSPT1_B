package com.myong.backend.domain.entity.business;

import com.myong.backend.domain.dto.shop.PaymentResponseDto;
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

    @Column(name = "p_pay_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // 결제 수단

    @Column(name = "p_price", nullable = false)
    private Long price; // 결제 금액

    @CreatedDate
    @Column(name = "p_pay_date")
    private LocalDateTime payDate; // 결제 날짜

    @Column(name = "p_reserv_menu_name", nullable = false)
    private String reservMenuName; // 예약한 메뉴명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "r_id", nullable = false)
    private Reservation reservation; // 예약 고유 키

    @Column(name = "p_success_yn")
    private boolean paySuccessYN; // 결제 성공 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user; // 유저 고유 키

    @Column(name = "p_payment_key")
    private String paymentKey; 

    @Column(name = "p_fail_reason")
    private String failReason; // 결제 실패 이유

    @Column(name = "p_cancel_yn")
    private boolean cancelYN; // 결제 취소 여부

    @Column(name = "p_cancel_reason")
    private String cancelReason; // 결제 취소 이유

    public PaymentResponseDto toPaymentResponseDto() {
        return PaymentResponseDto.builder()
                .paymentMethod(paymentMethod)
                .price(price)
                .reservMenuName(reservMenuName)
                .reservationId(reservation.getId())
                .userEmail(user.getEmail())
                .userName(user.getName())
                .payDate(payDate)
                .cancelYN(cancelYN)
                .failReason(failReason)
                .build();
    }

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

    public void assignUser(User user) {
        this.user = user;
    }

    public void successUpdate(String paymentKey) {
        this.paymentKey = paymentKey;
        this.paySuccessYN = true;
    }

    public void failUpdate(String message) {
        this.failReason = message;
        this.paySuccessYN = false;
    }
}
