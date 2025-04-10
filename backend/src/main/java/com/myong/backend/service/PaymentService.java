package com.myong.backend.service;

import com.myong.backend.domain.entity.business.Payment;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.repository.PaymentRepository;
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

    public Payment requestTossPayment(Payment payment, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        payment.assignUser(user);
        return paymentRepository.save(payment);
    }
}
