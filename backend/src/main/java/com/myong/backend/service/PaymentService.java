package com.myong.backend.service;

import com.myong.backend.domain.dto.shop.PaymentRequestDto;
import com.myong.backend.domain.dto.shop.PaymentResponseDto;
import com.myong.backend.domain.entity.business.Payment;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.repository.PaymentRepository;
import com.myong.backend.repository.ReservationRepository;
import com.myong.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public PaymentResponseDto requestTossPayment(PaymentRequestDto request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("해당 예약이 없습니다."));

        Payment payment = Payment.builder()
                .paymentMethod(request.getPaymentMethod())
                .price(request.getPrice())
                .reservMenuName(request.getReservMenuName())
                .paySuccessYN(false)
                .reservation(reservation)
                .build();

        payment.assignUser(user);
        Payment savedPayment = paymentRepository.save(payment);
        return savedPayment.toPaymentResponseDto();
    }
}
