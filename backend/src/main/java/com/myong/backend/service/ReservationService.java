package com.myong.backend.service;


import com.myong.backend.configuration.TossPaymentConfig;
import com.myong.backend.domain.dto.payment.*;
import com.myong.backend.domain.dto.reservation.MenuListData;
import com.myong.backend.domain.dto.reservation.response.*;
import com.myong.backend.domain.dto.shop.PaymentRequestDto;
import com.myong.backend.domain.dto.shop.PaymentResponseDto;
import com.myong.backend.domain.entity.business.Payment;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.DesignerHoliday;
import com.myong.backend.domain.entity.designer.DesignerRegularHoliday;
import com.myong.backend.domain.entity.shop.Menu;
import com.myong.backend.domain.entity.shop.MenuCategory;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.CouponStatus;
import com.myong.backend.domain.entity.user.DiscountType;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.exception.ResourceNotFoundException;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.repository.*;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.myong.backend.service.ShopService.getAuthenticatedEmail;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MenuRepository menuRepository;
    private final CouponRepository couponRepository;
    private final DesignerRepository designerRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final PaymentRepository paymentRepository;
    private final DesignerRegularHolidayRepository designerRegularHolidayRepository;
    private final DesignerHolidayRepository designerHolidayRepository;
    private final TossPaymentConfig tossPaymentConfig;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(10); // Redis에 저장할 TTL

    /**
     * 결제하기 버튼 -> 임시 예약 데이터 및 결제 객체 생성
     * @param request 결제 요청 정보를 담은 DTO
     * @return 생성한 결제 객체의 관련 정보를 담은 DTO
     */
    @Transactional
    public PaymentResponseDto createReservation(PaymentRequestDto request) {
        // 인증정보에서 유저 이메일 꺼내기
        String userEmail = getAuthenticatedEmail();

        // 유저 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));

        // 메뉴 조회
        Menu menu = menuRepository.findById(UUID.fromString(request.getMenuId()))
                .orElseThrow(() -> new RuntimeException("해당 메뉴를 찾을 수 없습니다"));

        // 디자이너 조회
        Designer designer = designerRepository.findByEmail(request.getDesignerEmail())
                .orElseThrow(() -> new RuntimeException("해당 디자이너를 찾을 수 없습니다"));

        // 사업자 조회
        Shop shop = shopRepository.findByEmail(request.getShopEmail())
                .orElseThrow(() -> new RuntimeException("해당 가게를 찾을 수 없습니다"));

        // 쿠폰 조회, 쿠폰이 요청에 존재하는 경우 할인타입에 따라 금액에 로직 적용하고, 없으면 금액에 로직 적용 X
        Long price = null;
        if(request.getCouponId() != null && !request.getCouponId().isBlank()) {
            Coupon coupon = couponRepository.findById(UUID.fromString(request.getMenuId()))
                    .orElseThrow(() -> new RuntimeException("해당 쿠폰을 찾을 수 없습니다"));

            price = coupon.getType().equals(DiscountType.FIXED)
                    ? request.getPrice() - coupon.getPrice()
                    : Math.round(request.getPrice() * (coupon.getPrice() * 0.01));
        } else {
            price = request.getPrice();
        }

        // 만약 쿠폰 로직을 통과한 후 금액이 0원 이하라면, 0원으로 세팅
        if (price < 0) price = 0L;

        // 결제 객체 생성 및 저장
        Payment payment = Payment.builder()
                .price(price)
                .reservMenuName(menu.getName())
                .paySuccessYN(false)
                .user(user)
                .designer(designer)
                .shop(shop)
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        // 임시 예약 데이터 빌더
        TempReservationData.TempReservationDataBuilder tempReservationBuilder = TempReservationData.builder()
                .serviceDate(request.getServiceDate())
                .designerEmail(request.getDesignerEmail())
                .shopEmail(request.getShopEmail())
                .menuId(request.getMenuId())
                .price(price.intValue());

        // 쿠폰이 요청에 존재하는 경우에만 빌더에 세팅 추가
        if (request.getCouponId() != null && !request.getCouponId().isBlank()) {
            tempReservationBuilder.couponId(request.getCouponId());
        }

        // 임시 예약 데이터 생성
        TempReservationData tempReservation = tempReservationBuilder.build();

        // Redis에 임시 예약 데이터 저장
        saveTempReservation(savedPayment.getId(), tempReservation);

        // 응답 DTO 생성 후 반환
        return PaymentResponseDto.builder()
                .price(savedPayment.getPrice())
                .reservMenuName(savedPayment.getReservMenuName())
                .paymentId(savedPayment.getId())
                .userEmail(savedPayment.getUser().getEmail())
                .userName(savedPayment.getUser().getName())
                .createDate(savedPayment.getCreateDate())
                .successUrl(tossPaymentConfig.getSuccessUrl())
                .failUrl(tossPaymentConfig.getFailUrl())
                .build();
    }


    /**
     * Redis에 임시 예약 저장
     * @param paymentId 결제 고유 키
     * @param data 임시 예약 데이터
     */
    private void saveTempReservation(UUID paymentId, TempReservationData data) {
        String key = "temp:reservation:" + paymentId;
        redisTemplate.opsForValue().set(key, data, TTL);
    }

    /**
     * Redis에 저장된 임시 예약 조히
     * @param paymentId 결제 고유 키
     */
    private TempReservationData getTempReservation(UUID paymentId) {
        String key = "temp:reservation:" + paymentId;
        return (TempReservationData)redisTemplate.opsForValue().get(key);
    }


    /**
     * 결제 인증 성공 시 -> 예약 생성
     * @param paymentKey 결제 요청과 인증이 완료되면 토스페이먼츠에서 결제를 식별하기 위해 발급하는 값
     * @param paymentId 결제 객체의 고유 키
     * @param amount 결제한 액수
     * @return 결제 인증 성공 관련 정보를 담은 DTO
     */
    @Transactional
    public PaymentSuccessDto tossPaymentSuccess(String paymentKey, String paymentId, Long amount) {
        // 결제 검색
        Payment payment = paymentRepository.findById(UUID.fromString(paymentId))
                .orElseThrow(() -> new RuntimeException("해당 결제가 없습니다."));

        // 결제 요청 당시 저장한 결제 객체의 금액과, 결제 인증 후 Toss가 보내준 결제 금액이 정확히 일치하는지 검증
        if(!payment.getPrice().equals(amount)) {
            throw new RuntimeException(
                    String.format("결제 금액 불일치: 서버 저장 금액 = %d, Toss 결제 금액 = %d", payment.getPrice(), amount)
            );
        }

        // Redis에서 임시 예약 데이터 조회
        TempReservationData temp = getTempReservation(UUID.fromString(paymentId));
        if (temp == null) throw new RuntimeException("임시 예약 정보가 없습니다. paymentId = " + paymentId);

        // 각 엔티티 조회
        User user = payment.getUser();
        Menu menu = menuRepository.findById(UUID.fromString(temp.getMenuId()))
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다"));
        Designer designer = designerRepository.findByEmail(temp.getDesignerEmail())
                .orElseThrow(() -> new RuntimeException("디자이너를 찾을 수 없습니다"));
        Shop shop = shopRepository.findByEmail(temp.getShopEmail())
                .orElseThrow(() -> new RuntimeException("샵을 찾을 수 없습니다"));

        // 쿠폰은 optional
        Coupon coupon = null;
        if (temp.getCouponId() != null && !temp.getCouponId().isBlank()) {
            coupon = couponRepository.findById(UUID.fromString(temp.getCouponId()))
                    .orElseThrow(() -> new RuntimeException("쿠폰을 찾을 수 없습니다"));
        }

        // 조회한 객체들을 토대로 예약 객체 생성 및 저장
        Reservation reservation = new Reservation(
                temp.getServiceDate(),
                temp.getPrice(),
                menu,
                shop,
                designer,
                user,
                coupon
        );
        Reservation savedReservation = reservationRepository.save(reservation);

        // 결제 객체 상태를 업데이트
        payment.successUpdate(paymentKey, reservation.getId());

        // 예약 임시 데이터 제거(임시 데이터가 시간이 지나 없어도 예외 발생 X)
        redisTemplate.delete("temp:reservation:" + paymentId);

        // 결제 인증을 성공했으므로, 결제 승인 요청
        requestTossPaymentApproval(paymentKey, paymentId, amount);

        // 성공한 결제 및 예약 관련 정보를 반환
        return PaymentSuccessDto.builder()
                .shopName(shop.getName())
                .menuName(payment.getReservMenuName())
                .price(payment.getPrice())
                .date(payment.getCreateDate())
                .build();
    }

    /**
     * 토스 결제 승인 API로 요청을 보내는 메서드
     * @param paymentKey 결제 승인에 필요한 토스 결제 키
     * @param paymentId 결제 엔티티의 고유 키 (요청 시 orderId로 사용)
     * @param amount 결제할 금액
     * @return 결제 성공 정보를 담은 PaymentSuccessDto 객체
     * @throws RuntimeException 토스 결제 API 호출 중 오류가 발생한 경우
     */
    public PaymentApprovalDto requestTossPaymentApproval(String paymentKey, String paymentId, Long amount) {
        // HTTP 요청을 보내기 위한 RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 요청에 필요한 헤더 설정 (Authorization, Content-Type 등)
        HttpHeaders headers = getHeaders();

        // HTTP 요청 바디에 들어갈 파라미터(orderId, amount)를 JSON 객체로 생성
        JSONObject params = new JSONObject();
        params.put("orderId", paymentId); // 주문 ID <- 결제 엔티티의 고유키로 세팅, 토스 서버에서 유일해야 함 -> 예약 한 건에 결제를 취소했다가 여러 번 다시 결제를 할 수 있게 해야한다.
        params.put("amount", amount); // 결제 금액 세팅

        // 결제 성공 결과를 담을 객체 초기화
        PaymentApprovalDto result = null;

        try {
            // 토스 결제 승인 API에 POST 요청을 보내고 응답을 PaymentSuccessDto로 파싱
            result = restTemplate.postForObject(
                    TossPaymentConfig.URL + paymentKey,           // 요청 URL (paymentKey 포함)
                    new HttpEntity<>(params, headers),            // 요청 바디와 헤더
                    PaymentApprovalDto.class                       // 응답을 매핑할 DTO 클래스
            );
        } catch (Exception e) {
            // API 호출 실패 시 예외 발생 (오류 메시지 포함)
            throw new RuntimeException("토스 결제 API 요청 중 오류 발생: " + e.getMessage());
        }

        // 결제 성공 결과 반환
        return result;
    }


    /**
     * 결제 인증 실패 시
     * @param code 실패 코드
     * @param paymentId 결제 객체의 고유 키
     * @param message 실패한 이유를 담은 메시지
     * @return 결제 인증 실패 관련 정보를 담은 DTO
     */
    @Transactional
    public PaymentFailDto tossPaymentFail(String code, String paymentId, String message) {
        // 결제 검색
        Payment payment = paymentRepository.findById(UUID.fromString(paymentId))
                .orElseThrow(() -> new RuntimeException("해당 결제가 없습니다."));

        // Redis의 임시 예약 데이터 삭제
        redisTemplate.delete("temp:reservation:" + paymentId);

        // 찾은 결제 객체의 상태를 실패상태로 업데이트
        payment.failUpdate(message);

        // 결제 실패정보 담은 DTO 반환
        return PaymentFailDto.builder()
                .errorCode(code)
                .paymentId(paymentId)
                .errorMessage(message)
                .build();
    }


    /**
     * 토스 결제 취소 API로 요청을 보내는 메서드
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
     * 토스 결제 승인 및 취소 요청을 위한 헤더 설정
     * @return 헤더
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
     * 사업자 예약 거절(결제 취소)
     * @param reservationId 거절의 예약의 고유 키
     * @param cancelReason 취소 이유
     * @return 결제 취소 결과를 담은 Map 객체
     */
    public Map refuseReservation(String reservationId, String cancelReason) {
        // 결제 검색
        Reservation reservation = reservationRepository.findById(UUID.fromString(reservationId))
                .orElseThrow(() -> new RuntimeException("해당 예약을 찾을 수 없습니다."));

        // 결제 검색
        Payment payment = paymentRepository.findByReservationId(reservation.getId())
                .orElseThrow(() -> new RuntimeException("해당 결제를 찾을 없습니다."));

        // 결제 취소 요청 메서드 호출 -> Map<String,String>을 반환
        Map map = requestTossPaymentCancel(payment.getPaymentKey(), cancelReason);

        // 결제 취소 성공 후, 만약 삭제할 예약에 쿠폰이 사용되었다면 -> 쿠폰의 만료 날짜가 아직 현재 날짜 이전인 경우, 새로운 미사용 상태 쿠폰을 만들어 다시 유저에게 돌려준 후 기존 쿠폰 객체 삭제
        Coupon coupon = reservation.getCoupon();
        if (coupon.getExpireDate().isBefore(LocalDate.now())) {
            Coupon newCoupon = coupon.toBuilder()
                    .status(CouponStatus.UNUSED)
                    .build();
            couponRepository.save(newCoupon);
            couponRepository.deleteById(coupon.getId());
        }

        // 예약 삭제
        reservationRepository.deleteById(reservation.getId());

        // 결제 객체의 상태를 업데이트
        payment.cancelUpdate(cancelReason);

        // 결제 취소 결과 반환
        return map;
    }

    /**
     * 유저의 결제 내역 조회
     * @return 결제 내역 정보를 담은 DTO들
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


    /**
     * 유저의 예약 내역 조회
     * @return
     */
    public List<ReservationInfoResponseDto> getReservationByUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Optional<User> ou = userRepository.findByEmail(userEmail);
        if(!ou.isPresent()){
            throw new ResourceNotFoundException("존재하지않는 유저입니다.");
        }

        User user = ou.get();
        List<Reservation> reservationList =  reservationRepository.findAllByUser(user);

        return reservationList.stream()
                .map(reservation -> new ReservationInfoResponseDto(
                    reservation.getServiceDate(),
                    reservation.getMenu().getName(),
                    reservation.getShop().getName(),
                    reservation.getDesigner().getName(),
                    reservation.getPrice()
                )).collect(Collectors.toList());

    }

    // 예약 페이지1

    public List<ReservationPage1ResponseDto> loadSelectDesignerPage(String email){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("현재 authentication : "+authentication);
        if(authentication == null || "anonymousUser".equals(authentication.getName())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"로그인이 필요한 기능입니다.");
        }

        Shop shop = shopRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("해당 가게가 존재하지 않습니다."));

        List<Designer> desingers = shop.getDesigners();
        List<ReservationPage1ResponseDto> responseDtos =
                desingers.stream().map(designer -> new ReservationPage1ResponseDto(
                        designer.getEmail(),
                        designer.getName(),
                        designer.getDesc(),
                        designer.getImage(),
                        designer.getRating(),
                        designer.getLike(),
                        designer.getReviewCount()
                )).collect(Collectors.toList());

        return responseDtos;
    }

    // 예약페이지 2번 (디자이너 시간선택)

    public ReservationPage2ResponseDto loadSelectTimePage(String email){

        Designer designer = designerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("해당 디자이너를 찾지 못했습니다."));

        // 정규 휴일 (ex: SUNDAY)
        DesignerRegularHoliday designerRegularHoliday = designerRegularHolidayRepository.findByDesigner(designer).orElse(null);
        String holidays = "";
        holidays += (designerRegularHoliday != null) ? designerRegularHoliday.getDay() : "";

        // 지정 휴일
        List<LocalDate> designerHolidays = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<DesignerHoliday> Holidays = designerHolidayRepository.findAllByDesignerAndMonth(designer,startDate,endDate);

        for(DesignerHoliday holiyday : Holidays){
            designerHolidays.add(holiyday.getDate());
        }

        // 예약 가능한 및 불가능한 시간 찾기
        List<LocalTime> availableTimes ;  // 가능한 시간
        List<LocalTime> unavailableTimes; // 불가능한 시간
        LocalTime openTime = designer.getShop().getOpenTime();
        LocalTime closeTime = designer.getShop().getCloseTime();
        availableTimes = getAvailableTimesAndUnavailableTimes_ByDesigner(LocalDate.now(),designer,openTime,closeTime)
                .get("available");
        unavailableTimes = getAvailableTimesAndUnavailableTimes_ByDesigner(LocalDate.now(),designer,openTime,closeTime)
                .get("unavailable");
        System.out.println(availableTimes);
        System.out.println(unavailableTimes);

        return new ReservationPage2ResponseDto(
                designer.getName(),
                designer.getDesc(),
                designer.getImage(),
                holidays,
                designerHolidays,
                availableTimes,
                unavailableTimes
        );
    }

    // 예약페이지2 디자이너 의 예약가능날짜 얻기

    public AvailableTimeResponseDto getAvailableTime(String designerEmail, LocalDate day){
         Designer designer = designerRepository.findByEmail(designerEmail).orElseThrow(() -> new ResourceNotFoundException("해당 디자이너를 찾을 수 없습니다."));

         LocalTime openTime = designer.getShop().getOpenTime();
         LocalTime closeTime = designer.getShop().getCloseTime();

         List<LocalTime> availableTimes = getAvailableTimesAndUnavailableTimes_ByDesigner(
                 day,designer,openTime,closeTime
         ).get("available");

         List<LocalTime> unavailableTimes = getAvailableTimesAndUnavailableTimes_ByDesigner(
                 day,designer,openTime,closeTime
         ).get("unavailable");

         return new AvailableTimeResponseDto(
                 availableTimes,
                 unavailableTimes
         );

    }


    // 예약페이지 3번 메뉴 선택페이지

    public SelectMenuResponseDto loadSelectMenuPage(String desingeremail){
        Designer designer = designerRepository.findByEmail(desingeremail).orElseThrow(() -> new ResourceNotFoundException("해당 디자이너를 찾을 수 없습니다."));


        // 1번~5번까지 커트,파마,염색,클리닉,스타일링 메뉴 초기화
        int categorySize = MenuCategory.values().length+1;
        List<MenuListData> [] responseMenus = new ArrayList[categorySize];
        for (int i = 0; i < categorySize; i++) {
            responseMenus[i] = new ArrayList<>();
        }


        // 카테고리 별로 리스트 뽑기
        for (MenuCategory category : MenuCategory.values()){
            if(category == MenuCategory.NONE){continue;}

            List<Menu> menus = menuRepository.findByDesignerAndCategory(designer,category);
            if(menus.size() > 0){
                responseMenus[category.ordinal()] = menus.stream().map(
                        Menu -> new MenuListData(
                                Menu.getId().toString(),
                                Menu.getName(),
                                Menu.getPrice(),
                                Menu.getEvent() != null ? discountPrice(Menu.getEvent().getPrice(),Menu.getPrice(),Menu.getEvent().getType()) : 0,
                                Menu.getEvent() != null ? Menu.getEvent().getPrice()+" "+( Menu.getEvent().getType() == DiscountType.PERCENT ? "%" : "원" )  : "",
                                Menu.getDesc(),
                                Menu.getImage()
                        )).collect(Collectors.toList());
            }
        }

        // 추천 메뉴 리스트 뽑기
        List<Menu> getRecommendMenus = menuRepository.findByDesignerAndRecommend(designer,true);
        List<MenuListData> recommendMenus = getRecommendMenus.stream().map(
                Menu -> new MenuListData(
                        Menu.getId().toString(),
                        Menu.getName(),
                        Menu.getPrice(),
                        Menu.getEvent() != null ? discountPrice(Menu.getEvent().getPrice(),Menu.getPrice(),Menu.getEvent().getType()) : 0,
                        Menu.getEvent() != null ? Menu.getEvent().getPrice()+" "+( Menu.getEvent().getType() == DiscountType.PERCENT ? "%" : "원" )  : "",
                        Menu.getDesc(),
                        Menu.getImage()
                )
        ).collect(Collectors.toList());



        return new SelectMenuResponseDto(responseMenus,recommendMenus, designer.getShop().getName(), designer.getShop().getEmail());

    }



    // 디자이너 로 예약 가능, 불가능 LocalTime 리스트 제공
    public Map<String,List<LocalTime>> getAvailableTimesAndUnavailableTimes_ByDesigner(
            LocalDate date,Designer designer, LocalTime openTime, LocalTime closeTime){

        LocalDateTime startDate = date.atTime(openTime);
        LocalDateTime endDate = date.atTime(closeTime);
        List<Reservation> reservations = reservationRepository.findByDesignerAndTime(designer,startDate,endDate);

        // 예약이된 시간대를 LocalTime 으로 변환
        List<LocalTime> servedTime = reservations.stream()
                .map(reservation -> reservation.getServiceDate().toLocalTime()).toList();


        List<LocalTime> availableTimes = new ArrayList<>();  // 가능한 시간
        List<LocalTime> unavailableTimes = new ArrayList<>(); // 불가능한 시간
        // 예약이 가능한 및 불가능한 시간대를 체크하고 저장
        LocalTime currentTime = openTime;
        while(currentTime.isBefore(closeTime)){
            if(!servedTime.contains(currentTime)){
                availableTimes.add(currentTime);
            }else{
                unavailableTimes.add(currentTime);
            }
            currentTime = currentTime.plusMinutes(30);
        }
        return Map.of("available", availableTimes, "unavailable", unavailableTimes);
    }


    public int discountPrice(int discount, int menuPrice, DiscountType discountType){
        if(discountType == DiscountType.FIXED){
            return discount;
        }
        return (int)(menuPrice * ((double)discount / 100));

    }



}
