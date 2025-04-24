package com.myong.backend.service;

import com.myong.backend.configuration.TossPaymentConfig;
import com.myong.backend.domain.dto.payment.PaymentHistoryDto;
import com.myong.backend.domain.dto.payment.PaymentSuccessDto;
import com.myong.backend.domain.dto.shop.PaymentRequestDto;
import com.myong.backend.domain.dto.shop.PaymentResponseDto;
import com.myong.backend.domain.entity.business.Payment;
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

import static com.myong.backend.service.ShopService.getAuthenticatedEmail;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final TossPaymentConfig tossPaymentConfig;

    /**
     * 결제 객체 생성
     * @param request 결제 요청 정보를 담은 DTO
     * @return
     */
    @Transactional
    public PaymentResponseDto requestTossPayment(PaymentRequestDto request) {
        // 인증정보에서 유저 이메일 꺼내기
        String userEmail = getAuthenticatedEmail();

        // 유저 검색
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));

        // 빌더 패턴을 통해 결제 도메인 객체 생성
        Payment payment = Payment.builder()
                .price(request.getPrice())
                .reservMenuName(request.getReservMenuName())
                .paySuccessYN(false)
                .user(user)
                .build();

        // 결제 객체를 영속성 컨텍스트에 저장
        Payment savedPayment = paymentRepository.save(payment);

        // 저장한 결제 객체의 정보들을 DTO에 담아 반환
        return PaymentResponseDto.builder()
                .price(savedPayment.getPrice())
                .reservMenuName(savedPayment.getReservMenuName())
                .paymentId(savedPayment.getId())
                .userEmail(savedPayment.getUser().getEmail())
                .userName(savedPayment.getUser().getName())
                .createDate(savedPayment.getCreateDate())
                .cancelYN(savedPayment.getCancelYN())
                .failReason(savedPayment.getFailReason())
                .successUrl(request.getYourSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl() : request.getYourSuccessUrl()) // 성공 URL이 따로 없으면 Config에서 가져온다
                .failUrl(request.getYourFailUrl() == null ? tossPaymentConfig.getFailUrl() : request.getYourFailUrl()) // 실패 URL이 따로 없으면 Config에서 가져온다s
                .build();
    }

    /**
     * 결제 성공
     * @param paymentKey
     * @param paymentId
     * @param amount
     * @return
     */
    @Transactional
    public PaymentSuccessDto tossPaymentSuccess(String paymentKey, String paymentId, Long amount) {
        Payment payment = verifyPayment(paymentId, amount); // 결제 요청정보 검증 메서드 -> 찾은 결제 객체 반환
        PaymentSuccessDto result = requestTossPaymentAccept(paymentKey, paymentId, amount); // 결제 요청 API 메서드 -> 결제 성공정보 담은 DTO 반환
        payment.successUpdate(paymentKey); // 찾은 결제 객체의 상태를 성공상태로 업데이트
        /**
         * 성공 후, 예약 테이블 생성 로직 들어올 곳(메서드 호출 등 다양한 방법 활용 가능)
         */
        return result; // 결제 성공정보 담은 DTO 반환
    }

    /**
     * 결제 요청정보 검증
     * @param paymentId
     * @param amount
     * @return
     */
    public Payment verifyPayment(String paymentId, Long amount) {
        // 결제 검색
        Payment payment = paymentRepository.findById(UUID.fromString(paymentId))
                .orElseThrow(() -> new RuntimeException("해당 결제가 없습니다."));

        // 찾은 결제 객체의 금액이 실제 금액과 다를 경우, 예외를 던진다
        if(!payment.getPrice().equals(amount)) {
            throw new RuntimeException("결제 금액 오류 payment.getPrice()  = " + payment.getPrice() + ", amount = " + amount);
        }
        
        // 찾은 결제 객체 반환
        return payment;
    }

    /**
     * 토스(Toss) 결제 승인 요청을 처리하는 메서드
     *
     * @param paymentKey 결제 승인에 필요한 토스 결제 키
     * @param paymentId 결제 엔티티의 고유 키 (요청 시 orderId로 사용)
     * @param amount 결제할 금액
     * @return 결제 성공 정보를 담은 PaymentSuccessDto 객체
     * @throws RuntimeException 토스 결제 API 호출 중 오류가 발생한 경우
     */
    public PaymentSuccessDto requestTossPaymentAccept(String paymentKey, String paymentId, Long amount) {
        // HTTP 요청을 보내기 위한 RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 요청에 필요한 헤더 설정 (Authorization, Content-Type 등)
        HttpHeaders headers = getHeaders();

        // HTTP 요청 바디에 들어갈 파라미터(orderId, amount)를 JSON 객체로 생성
        JSONObject params = new JSONObject();
        params.put("orderId", paymentId); // 주문 ID <- 결제 엔티티의 고유키로 세팅, 토스 서버에서 유일해야 함 -> 예약 한 건에 결제를 취소했다가 여러 번 다시 결제를 할 수 있게 해야한다.
        params.put("amount", amount); // 결제 금액 세팅

        // 결제 성공 결과를 담을 객체 초기화
        PaymentSuccessDto result = null;

        try {
            // 토스 결제 승인 API에 POST 요청을 보내고 응답을 PaymentSuccessDto로 파싱
            result = restTemplate.postForObject(
                    TossPaymentConfig.URL + paymentKey,           // 요청 URL (paymentKey 포함)
                    new HttpEntity<>(params, headers),            // 요청 바디와 헤더
                    PaymentSuccessDto.class                       // 응답을 매핑할 DTO 클래스
            );
        } catch (Exception e) {
            // API 호출 실패 시 예외 발생 (오류 메시지 포함)
            throw new RuntimeException("토스 결제 API 요청 중 오류 발생: " + e.getMessage());
        }

        // 결제 성공 결과 반환
        return result;
    }


    /**
     * 토스 결제 API 요청을 위한 헤더 설정
     * @return
     */
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = new String(Base64.getEncoder().encode((tossPaymentConfig.getTestSecretKey() + ":").getBytes(StandardCharsets.UTF_8)));
        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    /**
     * 결제 실패
     * @param code
     * @param paymentId
     * @param message
     */
    @Transactional
    public void tossPaymentFail(String code, String paymentId, String message) {
        // 결제 검색
        Payment payment = paymentRepository.findById(UUID.fromString(paymentId))
                .orElseThrow(() -> new RuntimeException("해당 결제가 없습니다."));

        // 찾은 결제 객체의 상태를 실패상태로 업데이트
        payment.failUpdate(message);
    }

    /**
     * 결제 취소
     * @param userEmail
     * @param paymentKey
     * @param cancelReason
     * @return
     */
    @Transactional
    public Map tossPaymentCancel(String userEmail, String paymentKey, String cancelReason) {
        // 결제 검색
        Payment payment = paymentRepository.findByPaymentKeyAndUser_Email(paymentKey, userEmail)
                .orElseThrow(() -> new RuntimeException("해당 결제가 없습니다."));

        // 찾은 결제 객체의 상태를 취소상태로 업데이트
        payment.cancelUpdate(cancelReason);

        // 결제 취소 요청 메서드 호출 -> Map<String,String>을 반환
        return requestTossPaymentCancel(paymentKey, cancelReason);
    }

    /**
     * 토스(Toss) 결제 취소 요청을 처리하는 메서드
     *
     * @param paymentKey 취소할 결제의 토스 결제 키
     * @param cancelReason 결제 취소 사유
     * @return 결제 취소 결과를 담은 Map 객체
     */
    private Map requestTossPaymentCancel(String paymentKey, String cancelReason) {
        // HTTP 요청을 보내기 위한 RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 요청에 필요한 헤더 정보 설정 (Authorization, Content-Type 등)
        HttpHeaders headers = getHeaders();

        // HTTP 요청 바디에 들어갈 파라미터(cancelReason)를 JSON 객체로 생성
        JSONObject params = new JSONObject();
        params.put("cancelReason", cancelReason); // 취소 사유 세팅

        // 토스 결제 취소 API로 POST 요청을 보내고 응답을 Map 타입으로 반환
        return restTemplate.postForObject(
                TossPaymentConfig.URL + paymentKey + "/cancel", // 요청 URL (결제 키와 /cancel 경로 포함)
                new HttpEntity<>(params, headers),              // 요청 바디와 헤더 설정
                Map.class                                       // 응답을 매핑할 타입 (Map)
        );
    }



    /**
     * 유저의 결제 내역 조회
     * @return
     */
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
