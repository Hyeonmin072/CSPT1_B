package com.myong.backend.service;

import com.myong.backend.configuration.TossPaymentConfig;
import com.myong.backend.domain.dto.payment.PaymentSuccessDto;
import com.myong.backend.domain.dto.shop.PaymentRequestDto;
import com.myong.backend.domain.dto.shop.PaymentResponseDto;
import com.myong.backend.domain.entity.business.Payment;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.repository.PaymentRepository;
import com.myong.backend.repository.ReservationRepository;
import com.myong.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final TossPaymentConfig tossPaymentConfig;

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

    @Transactional
    public PaymentSuccessDto tossPaymentSuccess(String paymentKey, String reservationId, Long amount) {
        Payment payment = verifyPayment(reservationId, amount);
        PaymentSuccessDto result = requestPaymentAccept(paymentKey, reservationId, amount);
        payment.successUpdate(paymentKey);
        return result;
    }

    public Payment verifyPayment(String reservationId, Long amount) {
        Reservation reservation = reservationRepository.findById(UUID.fromString(reservationId))
                .orElseThrow(() -> new RuntimeException("해당 예약이 없습니다."));

        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new RuntimeException("해당 결제가 없습니다."));

        if(!payment.getPrice().equals(amount)) {
            throw new RuntimeException("결제 금액 오류 payment.getPrice()  = " + payment.getPrice() + ", amount = " + amount);
        }
        return payment;
    }

    public PaymentSuccessDto requestPaymentAccept(String paymentKey, String reservationId, Long amount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("orderId", reservationId);
        params.put("amount", amount);

        PaymentSuccessDto result = null;
        try {
            result = restTemplate.postForObject(TossPaymentConfig.URL + paymentKey, new HttpEntity<>(params, headers), PaymentSuccessDto.class);
        }catch (Exception e) {
            throw new RuntimeException("토스 결제 API 요청 중 오류 발생" + e.getMessage());
        }
        return result;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = new String(Base64.getEncoder().encode((tossPaymentConfig.getTestSecretKey() + ":").getBytes(StandardCharsets.UTF_8)));
        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
