package com.myong.backend.service;

import com.myong.backend.configuration.TossPaymentConfig;
import com.myong.backend.domain.dto.payment.PaymentHistoryDto;
import com.myong.backend.domain.dto.payment.PaymentSuccessDto;
import com.myong.backend.domain.dto.shop.PaymentRequestDto;
import com.myong.backend.domain.dto.shop.PaymentResponseDto;
import com.myong.backend.domain.entity.business.Payment;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.repository.PaymentRepository;
import com.myong.backend.repository.ReservationRepository;
import com.myong.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final TossPaymentConfig tossPaymentConfig;

    @Transactional
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

    public void tossPaymentFail(String code, String reservationId, String message) {
        Reservation reservation = reservationRepository.findById(UUID.fromString(reservationId))
                .orElseThrow(() -> new RuntimeException("해당 예약이 없습니다."));

        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new RuntimeException("해당 결제가 없습니다."));

        payment.failUpdate(message);
    }

    @Transactional
    public Map tossPaymentCancel(String userEmail, String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPaymentKeyAndUser_Email(paymentKey, userEmail)
                .orElseThrow(() -> new RuntimeException("해당 결제가 없습니다."));

        payment.cancelUpdate(cancelReason);
        return cancel(paymentKey, cancelReason);
    }

    private Map cancel(String paymentKey, String cancelReason) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("cancelReason", cancelReason);

        return restTemplate.postForObject(TossPaymentConfig.URL + paymentKey + "/cancel", new HttpEntity<>(params, headers), Map.class);
    }

    @Transactional
    public List<PaymentHistoryDto> findAllChargingHistories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal = (UserDetailsDto)authentication.getPrincipal();
        String userEmail = principal.getUsername();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        ArrayList<Payment> payments = paymentRepository.findByUser(user);

        return payments.stream().map((p) -> PaymentHistoryDto.builder()
                .price(p.getPrice())
                .reservationName(p.getReservMenuName())
                .isPaySuccessYN(p.getPaySuccessYN())
                .createDate(p.getCreateDate())
                .build()
        ).toList();
    }
}
