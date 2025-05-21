package com.myong.backend.service;

import com.myong.backend.api.KakaoMapApi;
import com.myong.backend.domain.dto.coupon.CouponRequestDto;
import com.myong.backend.domain.dto.coupon.CouponResponseDto;
import com.myong.backend.domain.dto.event.EventRequestDto;
import com.myong.backend.domain.dto.event.EventResponseDto;
import com.myong.backend.domain.dto.job.JobPostDetailResponseDto;
import com.myong.backend.domain.dto.job.JobPostRequestDto;
import com.myong.backend.domain.dto.job.JobPostResponseDto;
import com.myong.backend.domain.dto.menu.MenuCreateRequestDto;
import com.myong.backend.domain.dto.menu.MenuDetailResponseDto;
import com.myong.backend.domain.dto.menu.MenuResponseDto;
import com.myong.backend.domain.dto.menu.MenuUpdateRequestDto;
import com.myong.backend.domain.dto.payment.DesignerLikeResponseDto;
import com.myong.backend.domain.dto.payment.DesignerSalesDetailResponseDto;
import com.myong.backend.domain.dto.payment.DesignerSalesResponseDto;
import com.myong.backend.domain.dto.payment.ShopSalesResponseDto;
import com.myong.backend.domain.dto.reservation.request.ShopReservationRequestDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationDetailResponseDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationJPAResponseDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationMyBatisResponseDto;
import com.myong.backend.domain.dto.shop.*;
import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.Period;
import com.myong.backend.domain.entity.business.Payment;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.DesignerHoliday;
import com.myong.backend.domain.entity.designer.DesignerRegularHoliday;
import com.myong.backend.domain.entity.shop.*;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.DiscountType;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.usershop.BlackList;
import com.myong.backend.exception.ExistSameEmailException;
import com.myong.backend.exception.NotEqualVerifyCodeException;
import com.myong.backend.exception.ResourceNotFoundException;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.repository.*;
import com.myong.backend.repository.mybatis.AttendanceMapper;
import com.myong.backend.repository.mybatis.ReservationMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ShopService {
    private final ShopBannerRepository shopBannerRepository;

    private final ShopRepository shopRepository;
    private final DefaultMessageService messageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CouponRepository couponRepository;
    private final EventRepository eventRepository;
    private final MenuRepository menuRepository;
    private final DesignerRepository designerRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final KakaoMapApi kakaoMapApi;
    private final JobPostRepository jobPostRepository;
    private final BlackListRepository blackListRepository;
    private final ReservationMapper reservationMapper;
    private final AttendanceMapper attendanceMapper;
    private final DesignerRegularHolidayRepository designerRegularHolidayRepository;
    private final DesignerHolidayRepository designerHolidayRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;
    private final NoticeRepository noticeRepository;
    private final SearchService searchService;
    private final FileUploadService fileUploadService;


    /**
     * 사업자 이메일 중복 확인
     * 이메일을 사용 중인지 확인하고 결과를 반환
     *
     * @param email 중복 확인할 이메일
     * @return 이메일 사용 가능 여부
     * @throws ExistSameEmailException 이미 사용 중인 이메일일 때 발생
     */
    public boolean checkEmail(String email) {
        Optional<Shop> findShop = shopRepository.findByEmail(email); // 이메일로 가게 찾기

        // 이미 있으면 예외 던지기
        return findShop.isPresent(); //false면 사용가능한 이메일
    }

    /**
     * 사업자 전화번호로 인증코드 발송
     * 인증코드를 전화번호로 발송하고 결과를 반환
     *
     * @param request 인증코드를 보낼 전화번호 정보가 담긴 DTO
     * @return 인증코드 전송 응답 객체
     */
    public SingleMessageSentResponse sendOne(ShopTelRequestDto request) {
        Message message = new Message(); // 메시지 객체 생성(외부 라이브러리에서 가져옴)
        message.setFrom("01033791271"); // 보낼 전화번호
        message.setTo(request.getTel()); // 받을 전화번호

        Random random = new Random();
        int verifyCode = 100000 + random.nextInt(900000); // 100000 ~ 999999사이 랜덤 코드 생성
        message.setText("[Hairism] 인증코드를 입력해주세요 : " + verifyCode); // 메시지 내용 설정

        // redis에 키, 값 각각 전화번호, 인증번호 형태로 저장, 5분의 시간제한 설정
        redisTemplate.opsForValue().set(request.getTel(), verifyCode, 5, TimeUnit.MINUTES);

        // 보내고 난 후의 응답 객체 반환
        return messageService.sendOne(new SingleMessageSendingRequest(message));
    }


    /**
     * 사업자 전화번호 인증코드 확인
     * 전화번호와 인증코드를 확인하고 결과를 반환
     *
     * @param request 인증코드 확인할 전화번호 정보가 담긴 DTO
     * @return 인증코드 확인 결과 메시지
     * @throws NotEqualVerifyCodeException 인증코드가 다를 경우 발생
     */
    public String checkVerifyCode(ShopVerifyCodeRequestDto request) {
        // redis에서 키로 값(인증번호) 꺼내기
        Integer verifyCode = (Integer) redisTemplate.opsForValue().get(request.getTel());

        // request의 인증코드가 redis에 저장된 값이랑 같을때
        if(verifyCode.equals(request.getVerifyCode())) return "인증이 완료되었습니다.";

            // request의 인증코드가 redis에 저장된 값이랑 인증코드가 다르면 예외 던지기
        else throw new NotEqualVerifyCodeException("인증코드가 일치하지 않습니다.");
    }

    /**
     * 사업자번호 인증 및 중복 확인
     * 사업자번호를 기반으로 중복 여부를 확인
     *
     * @param request 사업자번호 정보가 담긴 DTO
     * @return 인증 성공 메시지 또는 예외 발생
     * @throws RuntimeException 이미 사용 중인 사업자번호일 경우 발생
     */
    public String checkBiz(ShopBizRequestDto request) {
        Optional<Shop> shop = shopRepository.findByBizId(request.getBizId());


        if(shop.isEmpty()) return "사업자 정보가 확인되었습니다.";
        else throw new RuntimeException("이미 사용중인 사업자번호 입니다.");
    }


    /**
     * 사업자 회원가입
     * 회원 정보를 이용하여 회원가입 처리
     *
     * @param request 회원가입 요청 정보가 담긴 DTO
     * @return 회원가입 성공 메시지
     */
    @Transactional
    public String shopSignUp(ShopSignUpRequestDto request) {
        String result = kakaoMapApi.getCoordinatesFromAddress(request.getAddress());
        System.out.println("위도와 경도:" + result);
        String latitude = result.split(" ")[0];
        String longitude = result.split(" ")[1];

        // 가게 생성 후 저장
        Shop shop = new Shop(
                request.getName(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                request.getAddress(),
                request.getTel(),
                request.getBizId(),
                request.getPost(),
                Double.parseDouble(longitude),
                Double.parseDouble(latitude)
        );
        shopRepository.save(shop);

        // 엘라스틱 써치 도큐멘트 저장
        searchService.shopSave(shop);

        return "사업자 회원가입에 성공했습니다.";
    }

    /**
     * 사업자 로그아웃
     * 사업자 로그아웃 처리
     *
     * @return 회원가입 성공 메시지
     */
    public ResponseEntity<String> signOut(HttpServletResponse response) {
        String userEmail = getAuthenticatedEmail();
        try {
            if (redisTemplate.hasKey(userEmail)) {
                redisTemplate.delete(userEmail);
            }

            SecurityContextHolder.clearContext();


            ResponseCookie deleteCookie = ResponseCookie.from("accessToken",null)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(0)
                    .sameSite("Lax")
                    .build();

            response.addHeader("Set-Cookie",deleteCookie.toString());

        } catch (Exception e) {
            return ResponseEntity.status(400).body("로그아웃 요청 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok("로그아웃에 성공하셨습니다");
    }

    /**
     * 사업자 메인 페이지
     *
     * @return 메인 페이지에 필요한 정보 (오늘 남은 예약 인원, 이번 달 매출, 이번 달 매출 우수 디자이너, 이번 달 좋아요 우수 디자이너, 리뷰평점과 리뷰개수)
     */
    public ShopMainResponseDto getShopMain() {
        Long remainReservation = getReservationsToday(); // 오늘 남은 예약 인원
        Long monthSales = getShopSales(Period.ONE_MONTH).getTotalAmount(); // 이번 달 매출
        DesignerSalesResponseDto bestSalesDesigner = getBestSalesDesigner(); // 이번 달 매출 우수 디자이너
        DesignerLikeResponseDto bestLikeDesigner = getBestLikeDesinger(); // 이번 달 좋아요 우수 디자이너
        Shop shop = getShop(getAuthenticatedEmail()); // 로그인한 가게 조회
        Double rating = shop.getRating(); // 리뷰평점
        int reviewCount = shop.getReviews().size(); // 리뷰개수

        // DTO 반환
        return ShopMainResponseDto.builder()
                .remainReservation(remainReservation)
                .monthSales(monthSales)
                .bestSalesDesignerEmail(bestSalesDesigner != null ? bestSalesDesigner.getDesignerEmail() : "")
                .bestSalesDesignerName(bestSalesDesigner != null ? bestSalesDesigner.getDesignerName() : "")
                .bestSalesDesignerImage(bestSalesDesigner != null ? bestSalesDesigner.getDesignerImage() : "")
                .sales(bestSalesDesigner != null ? bestSalesDesigner.getDesignerSales() : 0L)
                .bestLikedesignerEmail(bestLikeDesigner != null ? bestLikeDesigner.getDesignerEmail() : "")
                .bestLikedesignerName(bestLikeDesigner != null ? bestLikeDesigner.getDesignerName() : "")
                .bestLikedesignerImage(bestLikeDesigner != null ? bestLikeDesigner.getDesignerImage() : "")
                .IncreasedLikes(bestLikeDesigner != null ? bestLikeDesigner.getIncreasedLikes() : 0L)
                .shopName(shop.getName())
                .rating(rating)
                .reviewCount(reviewCount)
                .build();

    }

    /**
     * 사업자 쿠폰 등록
     * 쿠폰 정보를 저장
     *
     * @param request 쿠폰 정보가 담긴 DTO
     * @return 쿠폰 등록 결과 메시지
     */
    public String addCoupon(CouponRequestDto request) {
        // 인증정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        Optional<Shop> findShop = shopRepository.findByEmail(email); // 이메일로 가게 찾기
        if(findShop.isEmpty()){
            throw new NoSuchElementException("해당 가게를 찾지못했습니다");
        }
        Shop shop = findShop.get();

        Coupon coupon = new Coupon( // 쿠폰 생성
                request.getName(),
                DiscountType.valueOf(request.getType()),
                request.getPrice(),
                request.getGetDate(),
                request.getUseDate(),
                shop
        );
        couponRepository.save(coupon); // 쿠폰 저장

        return "성공적으로 쿠폰이 등록되었습니다."; // 로직 수행결과 반환
    }

    /**
     * 사업자 쿠폰 조회
     * 등록된 쿠폰 목록을 반환
     *
     * @return 쿠폰 목록
     */
    public List<CouponResponseDto> getCoupons() {
        // 인증 정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);

        List<Coupon> coupons = couponRepository.findByShop(shop);// 가게를 통해 가져온 쿠폰들 반환
        List<CouponResponseDto> response = new ArrayList<>(); // 쿠폰 목록 리스트 생성
        for (Coupon coupon : coupons) { // 쿠폰 목록에 쿠폰 담기
            CouponResponseDto couponResponseDto = CouponResponseDto.builder().
                    id(coupon.getId().toString()).
                    name(coupon.getName()).
                    type(coupon.getType().toString()).
                    price(coupon.getPrice()).
                    getDate(coupon.getGetDate()).
                    useDate(coupon.getUseDate())
                    .build();
            response.add(couponResponseDto);
        }
        return response; // 쿠폰 목록 반환
    }

    /**
     * 사업자 쿠폰 마감 처리
     * 만료된 쿠폰 삭제
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteCoupon() {
        couponRepository.deleteByExpireDateBefore(LocalDate.now()); // 쿠폰들 삭제
        log.info("날짜가 지난 쿠폰 마감됨");// 로직 수행결과 반환
    }

    /**
     * 사업자 이벤트 등록
     * 이벤트 정보를 저장
     *
     * @param request 이벤트 등록 요청 정보가 담긴 DTO
     * @return 이벤트 등록 결과 메시지
     */
    public String addEvent(EventRequestDto request) {
        // 인증정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        // 가게 조회
        Shop shop = getShop(email);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd"); // 날짜 포매터 만들기
        Event event = new Event( // 이벤트 생성
                request.getName(),
                request.getPrice(),
                DiscountType.valueOf(request.getType()),
                LocalDate.parse(request.getStartDate(), formatter), // YYYY-MM-DD 형식으로 저장
                LocalDate.parse(request.getEndDate(), formatter), // YYYY-MM-DD 형식으로 저장
                shop
        );
        eventRepository.save(event); // 이벤트 저장

        return "성공적으로 이벤트가 등록되었습니다."; // 로직 수행결과 반환
    }

    /**
     * 사업자 이벤트 조회
     * 등록된 이벤트 목록을 반환
     *
     * @return 이벤트 목록
     */
    public List<EventResponseDto> getEvents() {
        // 로그인 인증 정보에서 이메일 가져오기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);

        List<Event> events = eventRepository.findByShop(shop);// 가게를 통해 가져온 이벤트들 반환
        List<EventResponseDto> response = new ArrayList<>(); // 이벤트 목록 리스트 생성
        for (Event event : events) { // 이벤트 목록에 이벤트 담기
            EventResponseDto eventResponseDto = EventResponseDto.builder().
                    id(event.getId().toString()).
                    name(event.getName()).
                    price(event.getPrice()).
                    type(event.getType().toString()).
                    startDate(event.getStartDate().toString()).
                    endDate(event.getEndDate().toString()).
                    build();
            response.add(eventResponseDto);
        }
        return response; // 이벤트 목록 반환
    }

    /**
     * 사업자 이벤트 종료
     * 만료된 이벤트 삭제
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteEvent() {
        eventRepository.deleteByEndDateBefore(LocalDate.now()); // 현재날짜보다 이전인 이벤트들 삭제
        log.info("날짜가 지난 이벤트 종료됨");// 로직 수행결과 반환
    }

    /**
     * 사업자 프로필 정보 조회
     * 사업자 프로필 정보를 반환
     *
     * @return 사업자 프로필 정보
     */
    public ShopProfileResponseDto getProfile() {
        // 로그인 인증 정보에서 이메일 가져오기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);

        // 가게 배너 이미지들 조회
        List<String> shopBannerImages = shop.getBanners().stream()
                .map(ShopBanner::getImage)
                .toList();

        return ShopProfileResponseDto.builder()
                .name(shop.getName())
                .address(shop.getAddress())
                .post(shop.getPost())
                .tel(shop.getTel())
                .pwd(shop.getPwd())
                .desc(shop.getDesc())
                .open(shop.getOpenTime().toString())
                .close(shop.getCloseTime().toString())
                .regularHoliday(shop.getRegularHoliday())
                .reservationNumber(shop.getUsers().size())
                .reviewNumber(shop.getReviewCount())
                .joinDate(shop.getCreateDate())
                .rating(shop.getRating())
                .thumbnail(shop.getThumbnail())
                .bannerImages(shopBannerImages)
                .build();
    }

    /**
     * 사업자 프로필 정보 수정
     * 기존 프로필 정보를 수정
     *
     * @param request 프로필 수정 요청 정보가 담긴 DTO
     * @return 프로필 수정 결과 메시지
     */
    @Transactional
    public String updateProflie(ShopProfileRequestDto request, MultipartFile thumbnail, List<MultipartFile> banner) {
        // 인증정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();
        Shop shop = getShop(email);
        String thumbnailUrl = "";  // 바뀐썸네일이 없으면 업데이트안함

        // 썸네일 url S3저장 및 추출
        if(thumbnail != null){
            String route = "shop" + "/" + email + "/" + "thumbnail" + "/";
            thumbnailUrl = fileUploadService.uploadFile(thumbnail,route);
            fileUploadService.deleteFile(shop.getThumbnail());
        }

        // 배너 추가 저장
        if(banner != null){
            for(MultipartFile file : banner){
                String route = "shop" + "/" + email + "/" + "banner" + "/";
                String bannerUrl = fileUploadService.uploadFile(file,route);
                shopBannerRepository.save(ShopBanner.save(bannerUrl,shop));
            }
        }

        shop.updateProfile(request,thumbnailUrl); // 찾은 가게의 프로필 정보 수정
        searchService.shopSave(shop);
        return "프로필이 수정되었습니다."; // 성공 구문 반환
    }

    /**
     * 배너 파일삭제
     *
     * @param url 삭제시킬
     * @return 성공메세지
     */
    public String deleteBanner(String url){
        ShopBanner shopBanner = shopBannerRepository.findByImage(url).orElseThrow(() -> new ResourceNotFoundException("해당 이미지를 찾지 못했습니다."));

        fileUploadService.deleteFile(shopBanner.getImage());
        shopBannerRepository.delete(shopBanner);
        return "파일이 성공적으로 삭제 되었습니다.";
    }

    /**
     * 사업자 메뉴 추가
     * 새로운 메뉴를 등록
     *
     * @param request 메뉴 등록 요청 정보가 담긴 DTO
     * @return 메뉴 등록 결과 메시지
     */
    public String createMenu(@Valid MenuCreateRequestDto request) {
        // 인증정보에서 사업자 이메일 꺼내고, 가게 조회
        String email = getAuthenticatedEmail();
        
        Shop shop = getShop(email);

        // 디자이너 당 메뉴 엔티티 개체 생성 후 저장
        for (String designerEmail : request.getDesignerEmails()) {
            Designer designer = getDesigner(designerEmail);

            Menu menu = Menu.builder()
                    .name(request.getName())
                    .desc(request.getDesc())
                    .price(request.getPrice())
                    .estimatedTime(request.getEstimatedTime())
                    .shop(shop)
                    .designer(designer)
                    .category(request.getCategory())
                    .build();

            menuRepository.save(menu);
        }

        return "성공적으로 메뉴가 등록되었습니다.";
    }

    /**
     * 사업자 소속 디자이너의 메뉴 목록 조회
     * 가게에 소속된 디자이너이 등록된 메뉴 목록을 반환
     *
     * @return 메뉴 목록
     */
    public List<MenuResponseDto> getMenus(String designerEmail) {
        Designer designer = getDesigner(designerEmail);
        return designer.getMenus().stream()
//                .map(m -> new MenuResponseDto(m.getId(), m.getName(), designer.getName(), m.getPrice(), m.getCategory()))
                .map(m -> MenuResponseDto.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .designerName(designer.getName())
                        .price(m.getPrice())
                        .category(m.getCategory())
                        .image(m.getImage())
                        .build())
                .toList();
    }

    /**
     * 사업자 메뉴 단건 조회
     * 등록된 메뉴 단건 개체 반환
     *
     * @param id 메뉴의 고유 키
     * @return 메뉴 목록
     */
    public MenuDetailResponseDto getMenu(String id) {
        Menu menu = menuRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("찾고자 하는 메뉴가 없습니다."));

        return MenuDetailResponseDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .designerName(menu.getDesigner().getName())
                .price(menu.getPrice())
                .desc(menu.getDesc())
                .image(menu.getImage())
                .category(menu.getCategory())
                .estimateTime(menu.getEstimatedTime())
                .build();
    }

    /**
     * 사업자 메뉴 수정
     * 기존 메뉴 정보 수정
     *
     * @param id 수정할 메뉴의 고유 키
     * @param request 메뉴 수정 요청 정보가 담긴 DTO
     * @return 메뉴 수정 결과 메시지
     */
    public String updateMenu(String id, MenuUpdateRequestDto request) {
        Menu menu = menuRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("해당 메뉴를 찾을 수 없습니다.")); // 메뉴 이이디로 찾기
        menu.edit(request); // 편의 메서드로 메뉴 정보 수정
        
        return "성공적으로 메뉴가 수정되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 메뉴 삭제
     * 메뉴 정보 삭제
     *
     * @param menuId 메뉴 삭제 요청 정보가 담긴 DTO
     * @return 메뉴 삭제 결과 메시지
     */
    public String deleteMenu(String menuId) {
        Menu menu = menuRepository.findById(UUID.fromString(menuId))
                .orElseThrow(() -> new NoSuchElementException("해당 메뉴를 찾을 수 없습니다.")); // 메뉴 이이디로 찾기
        menuRepository.delete(menu); // 메뉴 삭제
        return "성공적으로 메뉴가 삭제되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 구인글 등록
     * 새로운 구인글 등록
     *
     * @param request 구인글 등록 요청 정보가 담긴 DTO
     * @return 구인글 등록 결과 메시지
     */
    public String addJobPost(JobPostRequestDto request) {
        // 로그인 인증 정보에서 이메일 가져오기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm"); // 날짜 포매터 만들기
        JobPost jobPost = JobPost.builder() //빌더를 통해 구인글 생성
                .shop(shop)
                .title(request.getTitle())
                .gender(Gender.valueOf(request.getGender()))
                .work(Work.valueOf(request.getWork()))
                .workTime(LocalTime.parse(request.getWorkTime(), formatter))
                .leaveTime(LocalTime.parse(request.getWorkTime(), formatter))
                .content(request.getContent())
                .salary(request.getSalary())
                .build();

        jobPostRepository.save(jobPost); // 구인글 개체를 영속성 컨텍스트에 저장
        return "성공적으로 구인글이 등록되었습니다."; // 성공 구문 반환
    }


    /**
     * 사업자 구인글 목록 조회
     * 등록된 구인글 목록 반환
     *
     * @return 구인글 목록
     */
    public List<JobPostResponseDto> getJobPosts() {
        // 로그인 인증 정보에서 이메일 가져오기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);
        List<JobPost> jobPosts = jobPostRepository.findByShop(shop.getId());// 가게의 고유 키를 통해 가져온 구인글 목록 반환

        List<JobPostResponseDto> response = new ArrayList<>(); // 구인글 목록 리스트 생성
        for (JobPost jobPost : jobPosts) { // 구인글 목록에 구인글 목록 담기
            JobPostResponseDto jobPostResponseDto = JobPostResponseDto.builder().
                    shopName(jobPost.getShop().getName()).
                    id(jobPost.getId().toString()).
                    title(jobPost.getTitle()).
                    salary(jobPost.getSalary()).
                    gender(jobPost.getGender().toString()).
                    work(jobPost.getWork().toString()).
                    workTime(jobPost.getWorkTime().toString()).
                    leaveTime(jobPost.getLeaveTime().toString())
                    .build();
            response.add(jobPostResponseDto); // 구인글 목록 dto 반환
        }
        return response;
    }

    /**
     * 사업자 구인글 단건 조회
     * 등록된 구인글 목록 반환
     *
     * @param id 구인글의 고유 키
     * @return 구인글 개체 반환
     */
    public JobPostDetailResponseDto getJobPost(String id) {
        JobPost jobPost = jobPostRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("찾고자 하는 구인글이 없습니다."));

        return JobPostDetailResponseDto.builder()
                .shopName(jobPost.getShop().getName())
                .id(jobPost.getId())
                .title(jobPost.getTitle())
                .salary(jobPost.getSalary())
                .gender(jobPost.getGender())
                .work(jobPost.getWork())
                .workTime(jobPost.getWorkTime())
                .leaveTime(jobPost.getLeaveTime())
                .build();
    }

    /**
     * 사업자 구인글 수정
     * 기존 구인글 정보 수정
     *
     * @param id 수정할 구인글의 고유 키
     * @param request 구인글 수정 요청 정보가 담긴 DTO
     * @return 구인글 수정 결과 메시지
     */
    public String updateJobPost(String id, JobPostRequestDto request) {// 가게 찾기
        JobPost jobPost = jobPostRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("해당 구인글을 찾을 수 없습니다.")); // 구인글 아이디로 구인글 찾기
        jobPost.updateJobPost(request); // 구인글 수정 편의 메서드를 통해 수정
        return "성공적으로 구인글이 수정되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 구인글 삭제
     * 구인글 정보 삭제
     *
     * @param id 삭제할 구인글의 고유 키
     * @return 구인글 삭제 결과 메시지
     */
    public String deleteJobPost(String id) {
        JobPost jobPost = jobPostRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("해당 구인글을 찾을 수 없습니다.")); // 구인글 아이디로 구인글 찾기
        jobPostRepository.delete(jobPost); // 구인글 삭제
        return "성공적으로 구인글이 마감되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 소속 디자이너 추가
     * 소속 디자이너 등록
     *
     * @param request 디자이너 추가 요청 정보가 담긴 DTO
     * @return 디자이너 등록 결과 메시지
     */
    public String joinDesigner(ShopDesignerRequestDto request) {
        // 다자이너 조회
        Designer designer = getDesigner(request.getDesignerEmail());

        // 인증정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        // 가게 조회
        Shop shop = getShop(email);

        // 둘 다 찾으면 가게에 디자이너 추가
        designer.getJob(shop);

        // 가게에 디자이너가 추가된 후, 디자이너 정기 휴무일 생성 및 저장
        DesignerRegularHoliday designerRegularHoliday = DesignerRegularHoliday.builder()
                .designer(designer)
                .build();
        designerRegularHolidayRepository.save(designerRegularHoliday);

        // 디자이너의 위치정보를 가입된 가게의 것으로 변경
        designer.changeLocationByJoin(shop.getAddress(), shop.getLongitude(), shop.getLatitude());

        return "성공적으로 디자이너가 추가되었습니다.";
    }

    /**
     * 사업자 추가할 디자이너 정보 조회
     */
    public ShopDesignerDetailResponseDto searchDesigner(ShopDesignerRequestDto request) {
        // 디자이너 찾기
        Designer designer = getDesigner(request.getDesignerEmail());

        // 디자이너 상세정보를 dto에 담아 반환
        return ShopDesignerDetailResponseDto.builder()
                .name(designer.getName())
                .gender(designer.getGender().toString())
                .like(designer.getLike())
                .email(designer.getEmail())
                .tel(designer.getTel())
                .build();
    }

    /**
     * 사업자 소속 디자이너의 휴일 추가
     * 디자이너 휴일 정보 저장
     *
     * @param request 디자이너 휴일 추가 요청 정보가 담긴 DTO
     * @return 디자이너 휴일 추가 결과 메시지
     */
    public String createDesignerHoliday(ShopDesignerHolidayRequestDto request) {
        // 디자이너 찾기
        Designer designer = getDesigner(request.getDesignerEmail());

        // 디자이너 휴무일 생성 후 저장
        DesignerHoliday designerHoliday = DesignerHoliday.builder()
                .designer(designer)
                .date(request.getHoliday())
                .build();
        designerHolidayRepository.save(designerHoliday);

        return "성공적으로 소속 디자이너 휴일이 추가되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 소속 디자이너 목록 조회
     * 등록된 디자이너 목록 반환
     *
     * @return 디자이너 목록
     */
    public List<ShopDesignerResponseDto> getDesigners() {
        // 인증 정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);

        List<Designer> designers = shop.getDesigners();// 디자이너들 가져오기
        List<ShopDesignerResponseDto> dtos = new ArrayList<>();
        for (Designer designer : designers) { // 가져온 디자이너들의 정보를 dto에 담은 뒤 리스트로 반환
            ShopDesignerResponseDto dto = ShopDesignerResponseDto.builder()
                    .email(designer.getEmail())
                    .name(designer.getName())
                    .like(designer.getLike())
                    .gender(designer.getGender().toString())
                    .image(designer.getImage())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * 사업자 소속 디자이너 상세 조회
     * 특정 디자이너의 상세 정보 반환
     *
     * @param request 디자이너 상세 조회 요청 정보가 담긴 DTO
     * @return 디자이너 상세 정보
     */
    public ShopDesignerDetailResponseDto getDesignerDetail(ShopDesignerRequestDto request) {
        // 디자이너 찾기
        Designer designer = getDesigner(request.getDesignerEmail());

        // 디자이너 정기 휴무일 찾기
        DesignerRegularHoliday designerRegularHoliday = designerRegularHolidayRepository.findByDesigner(designer)
                .orElseThrow(() -> new NoSuchElementException("해당 디자이너의 정기 휴무일 정보를 찾을 수 없습니다."));

        // 디자이너 상세정보를 dto에 담아 반환
        return ShopDesignerDetailResponseDto.builder()
                .name(designer.getName())
                .gender(designer.getGender().toString())
                .like(designer.getLike())
                .email(request.getDesignerEmail())
                .workTime(designer.getWorkTime())
                .leaveTime(designer.getLeaveTime())
                .regularHoliday(designerRegularHoliday.getDay())
                .build();
    }

    /**
     * 사업자 소속 디자이너 수정
     * 디자이너 정보 및 정기 휴무일 수정
     *
     * @param request 디자이너 수정 요청 정보가 담긴 DTO
     * @return 디자이너 수정 결과 메시지
     */
    public String updateDesigner(ShopDesignerUpdateRequestDto request) {
        // 디자이너 찾기
        Designer designer = getDesigner(request.getDesignerEmail());

        DesignerRegularHoliday designerRegularHoliday = designerRegularHolidayRepository.findByDesigner(designer)
                .orElseThrow(() -> new NoSuchElementException("해당 디자이너의 정기 휴무일 정보를 찾을 수 없습니다."));// 디자이너 정기 휴무일 찾기


        designer.updateWorkAndLeave(request.getWorkTime(), request.getLeaveTime()); // 춡퇴근 시간 다르면 업데이트
        designerRegularHoliday.updateHoliday(request.getRegularHoliday()); // 정기 휴무일 다르면 업데이트
        return "성공적으로 소속 디자이너 정보가 수정되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 소속 디자이너 해고
     * 디자이너 소속 해제 및 관련 정보 삭제
     *
     * @param request 디자이너 삭제 요청 정보가 담긴 DTO
     * @return 디자이너 삭제 결과 메시지
     */
    public String fireDesigner(ShopDesignerRequestDto request) {
        // 디자이너 조회
        Designer designer = getDesigner(request.getDesignerEmail());

        // 인증정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        // 가게 조회
        getShop(email);

        // 둘 다 찾으면 디자이너 소속 해제 및 출퇴근 시간 초기화
        designer.fire();

        // 이제 가게 소속이 아니므로, 정기 휴무일, 휴무일, 근태 개체 삭제
        designerRegularHolidayRepository.deleteByDesigner(designer);
        designerHolidayRepository.deleteByDesigner(designer);
        attendanceRepository.deleteByDesigner(designer);

        // 디자이너의 위치정보를 초기화
        designer.changeLocationByFire();

        // 성공 구문 반환
        return "성공적으로 디자이너가 삭제되었습니다.";
    }

    /**
     * 사업자 블랙리스트 추가
     * 특정 유저를 블랙리스트에 등록
     *
     * @param request 블랙리스트 추가 요청 정보가 담긴 DTO
     * @return 블랙리스트 추가 결과 메시지
     */
    public String createBlackList(BlackListRequestDto request) {
        // 인증 정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        // 가게 조회
        Shop shop = getShop(email);

        // 유저 조회
        User user = getUser(request.getUserEmail());

        // 이미 블랙리스트에 등록되었는지 검증
        Optional<BlackList> findResult = blackListRepository.findByShopAndUser(shop, user);
        if (findResult.isPresent()) throw new RuntimeException("이미 블랙리스트에 추가된 유저입니다.");

        // 등록되지 않았을 경우 -> 블랙리스트 개체 생성 후 저장
        BlackList blackList = BlackList.builder()
                .shop(shop)
                .user(user)
                .reason(request.getReason())
                .build();

        blackListRepository.save(blackList);

        // 성공 구문 반환
        return "성공적으로 블랙리스트에 추가되었습니다.";
    }

    /**
     * 사업자 블랙리스트 목록 조회
     * 등록된 블랙리스트 목록 반환
     *
     * @return 블랙리스트 목록
     */
    public List<BlackListResponseDto> getBlackLists() {
        // 인증 정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        //가게 조회
        Shop shop = getShop(email);

        // 가게의 블랙리스트 조회
        List<BlackList> blackLists = blackListRepository.findByShop(shop);

        // 가져온 블랙리스트들을 각각 담은 뒤 DTO 리스트로 반환
        List<BlackListResponseDto> dtos = new ArrayList<>();
        for (BlackList blackList : blackLists) {
            BlackListResponseDto dto = BlackListResponseDto.builder()
                    .blackListId(blackList.getId())
                    .reason(blackList.getReason())
                    .userName(blackList.getUser().getName())
                    .userEmail(blackList.getUser().getEmail())
                    .build();
            dtos.add(dto);
        }

        return dtos;
    }

    /**
     * 사업자 블랙리스트 단건 조회
     * 등록된 블랙리스트 목록 반환
     *
     * @param id 블랙리스트 개체의 아이디
     * @return 블랙리스트 개체체 정보
     */
    public BlackListResponseDto getBlackList(String id) {
        // 인증 정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        //가게 조회
        Shop shop = getShop(email);

        // 가게의 블랙리스트 조회
        BlackList blackList = blackListRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("찾고자하는 블랙리스트가 없습니다."));

        // 가져온 블랙리스트들을 각각 담은 뒤 DTO 리스트로 반환
        return BlackListResponseDto.builder()
                .blackListId(blackList.getId())
                .reason(blackList.getReason())
                .userName(blackList.getUser().getName())
                .userEmail(blackList.getUser().getEmail())
                .build();

    }

    /**
     * 사업자 블랙리스트 삭제
     * 블랙리스트에서 특정 사용자 정보 삭제
     *
     * @param userEmails 블랙리스트 삭제 요청 정보가 담긴 DTO 리스트
     * @return 블랙리스트 삭제 결과 메시지
     */
    public String deleteBlackList(List<String> userEmails) {
        for (String userEmail : userEmails) {
            // 인증 정보에서 사업자 이메일 꺼내기
            String email = getAuthenticatedEmail();

            // 가게 조회
            Shop shop = getShop(email);

            // 유저 조회
            User user = getUser(userEmail);

            // 찾은 가게와 유저를 통해 해당 블랙리스트 개체 찾기
            BlackList blackList = blackListRepository.findByShopAndUser(shop, user)
                    .orElseThrow(() -> new NoSuchElementException("해당 블랙리스트를 찾을 수 없습니다."));

            // 블랙리스트 개체 삭제
            blackListRepository.delete(blackList);
        }

        // 성공 구문 반환
        return "성공적으로 블랙리스트에서 삭제되었습니다.";
    }

    /**
     * 사업자 예약 조회
     * 특정 조건에 해당하는 예약 목록 반환
     *
     * @param request 예약 조회 요청 정보가 담긴 DTO
     * @return 예약 목록
     */
    public List<ShopReservationMyBatisResponseDto> getReservations(ShopReservationRequestDto request) {
        // 인증 정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        // 가게 찾기
        getShop(email);
        // MyBatis SqlMapper를 통해 예약 조회하기
        return reservationMapper.findAll(email, request);
    }

    /**
     * 사업자 예약 상세 조회
     * 특정 예약의 상세 정보 반환
     *
     * @param reservationId 예약의 고유 ID
     * @return 예약 상세 정보
     */
    public ShopReservationDetailResponseDto getReservation(UUID reservationId) {
        // 예약 상세 조회 결과 반환
        return reservationRepository.findDetailById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("해당 예약을 찾을 수 없습니다."));
    }

    /**
     * 지난 7일 간의 예약 조회 (블랙리스트 추가를 위한 조회 시 사용)
     * @return 최근 7일 간의 예약 목록
     */
    public List<ShopReservationJPAResponseDto> getLastSevenDaysReservation() {
        // 인증 정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        // 가게 찾기
        Shop shop = getShop(email);

        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return reservationRepository.findLastSevenDays(startDate, shop);
    }

    /**
     * 사업자 오늘 남은 예약 개수 조회
     * 오늘 날짜와 시분초를 기준으로 하루동안 남은 예약건 수를 조회 -> 1, 3, 5
     *
     * @return 예약 상세 정보
     */
    public Long getReservationsToday() {
        String email = getAuthenticatedEmail(); // 로그인 인증 정보에서 이메일 꺼내기
        Shop shop = getShop(email); // 꺼낸 이메일 -> 가게 조회

        // 찾은 가게로 전체 예약 조회
        List<Reservation> reservations = reservationRepository.findByShop(shop);

        // 스트림을 이용해 오늘 중 현재시각 이후의 예약들을 가져온 후 항목 갯수 반환
        return reservations.stream()
                .filter(r -> r.getServiceDate().toLocalDate().equals(LocalDate.now())) // 오늘 날짜로 필터링
                .filter(r -> r.getServiceDate().isAfter(LocalDateTime.now())) // 현재 시점 이후 필터링
                .count();
    }

    /**
     * 사업자 근태 상세 조회
     * 특정 조건에 해당하는 근태 목록 반환
     *
     * @param request 근태 조회 요청 정보가 담긴 DTO
     * @return 근태 목록
     */
    public List<ShopAttendanceResponseDto> getAttendance(ShopAttendanceRequestDto request) {
        // 가게 찾기
        return attendanceMapper.findAll(request);
    }

    /**
     * 공지사항 생성
     * 현재 로그인한 사업자의 이메일과 공지사항 관련 정보를 가지고 공지사항 개체 생성
     * @param request 공지사항 개체 생성에 필요한 정보가 담긴 DTO
     * @return 성공 구문 반환
     */
    public String createNotice(ShopNoticeRequestDto request) {
        String email = getAuthenticatedEmail(); // 로그인 인증 정보에서 이메일 꺼내기
        Shop shop = getShop(email); // 꺼낸 이메일 -> 가게 조회

        Notice notice = Notice.builder() // 공지사항 객체 생성 후 -> 저장(영속성 컨텍스트)
                .title(request.getTitle())
                .content(request.getContent())
                .shop(shop)
                .importance(request.getImportance())
                .build();
        noticeRepository.save(notice);

        return "공지사항이 성공적으로 생성되었습니다.";
    }

    /**
     * 공지사항 전체 조회
     * 현재 로그인한 사업자의 이메일을 가지고 전체 공지사항 개체 조회
     * @return 공지사항 정보를 담은 DTO들을 반환
     */
    public List<ShopNoticeResponseDto> getNotices() {
        String email = getAuthenticatedEmail(); // 로그인 인증 정보에서 이메일 꺼내기
        Shop shop = getShop(email); // 꺼낸 이메일 -> 가게 조회

        List<Notice> notices = noticeRepository.findByShop(shop); // 조회한 가게 객체를 통해 공지사항 조회

        // 스트림을 통해 Notice -> DTO 객체로 map 중간연산을 통해 변환한 뒤 반환
        return notices.stream()
                .map((n) -> ShopNoticeResponseDto.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .createDate(n.getCreateDate())
                        .importance(n.getImportance())
                        .build())
                .toList();
    }

    /**
     * 공지사항 단건 조회
     * 전체 공지사항 개체 중 단건 조회
     * @param id 조회하고자 하는 공지사항 개체의 고유 키
     * @return 공지사항 상세정보를 담은 DTO를 반환
     */
    public ShopNoticeDetailResponseDto getNotice(String id) {
        Notice notice = noticeRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("찾고자 하는 공지사항 글이 없습니다."));

        return ShopNoticeDetailResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createDate(notice.getCreateDate())
                .importance(notice.getImportance())
                .build();
    }

    /**
     * 가장 최신의 공지사항 단건 조회
     * 현재 로그인한 사업자의 이메일을 가지고 전체 공지사항 중 가장 최신의 공지사항 개체 조회
     * @return 공지사항 상세정보를 담은 DTO를 반환
     */
    public ShopNoticeDetailResponseDto getNoticeLatest() {
        String email = getAuthenticatedEmail(); // 로그인 인증 정보에서 이메일 꺼내기
        Shop shop = getShop(email); // 꺼낸 이메일 -> 가게 조회

        List<Notice> notices = noticeRepository.findByShop(shop); // 조회한 가게 객체를 통해 공지사항 조회

        // 스트림을 통해 Notice 리스트 -> sorted 중간 연산을 통해 생성일 내림차순으로 정렬 -> 첫번째 항목 찾기
        Notice notice = notices.stream()
                .sorted(Comparator.comparing(Notice::getCreateDate).reversed())
                .findFirst()
                .orElse(null);

        // 찾은 Notice -> DTO로 반환
        return ShopNoticeDetailResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createDate(notice.getCreateDate())
                .importance(notice.getImportance())
                .build();
    }

    /**
     * 공지사항 단건 수정
     * 현재 로그인한 사업자의 이메일을 가지고 전체 공지사항 중 가장 최신의 공지사항 개체 조회
     * @param id 조회하고자 하는 공지사항 개체의 고유 키
     * @param request 공지사항 개체 수정에 필요한 정보가 담긴 DTO
     * @return 성공 구문 반환
     */
    public String updateNotice(String id, ShopNoticeRequestDto request) {
        Notice notice = noticeRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("찾고자 하는 공지사항 글이 없습니다."));

        // Notice 도메인 객체(엔티티)의 수정 편의 메서드 호출 후 성곤구문 반환
        notice.update(request);
        return "성공적으로 공지사항이 수정되었습니다.";
    }

    /**
     * 공지사항 단건 삭제
     * 현재 로그인한 사업자의 이메일을 가지고 전체 공지사항 중 가장 최신의 공지사항 개체 조회
     * @return 공지사항 상세정보를 담은 DTO를 반환
     */
    public String deleteNotice(String id) {
        Notice notice = noticeRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("찾고자 하는 공지사항 글이 없습니다."));

        // 삭제 후 성공구문 반환
        noticeRepository.delete(notice);
        return "성공적으로 공지사항이 삭제되었습니다.";
    }

    /**
     * 사업자 가게 매출 조회
     * @param period 추가 검샊 기간 ONE_WEEK, ONE_MONTH, ONE_YEAR 각각 최근 1주일, 1달, 1년
     * @return 가게 매출 정보를 담은 DTO
     */
    public ShopSalesResponseDto getShopSales(Period period) {
        // 로그인 정보에서 가게 이메일을 꺼내고, 가게 조회
        String shopEmail = getAuthenticatedEmail();
        Shop shop = getShop(shopEmail);

        // 가게의 결제가 성공상태이며, 취소되지 않은 것들만 필터링
        List<Payment> payments = shop.getPayments().stream()
                .filter(p -> p.getPaySuccessYN() && p.getCancelYN() == null)
                .toList();

        // 이번 주, 이번 달, 이번 해 기준에 따라 필터링한 결과의 총 금액 계산
        // 기본값 세팅
        long totalAmount = 0L;
        Map<String, Long> graph = Map.of();
        LocalDate now = LocalDate.now();

        if (period.equals(Period.ONE_WEEK)) { // 이번 주인 경우 -> ONE_WEEK
            LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // 현재 날짜가 소속된 주의 월요일인 날짜 가져오기

            // 현재 날짜가 소속된 주의 총 금액 계산
            totalAmount = payments.stream()
                    .filter(p -> p.getCreateDate().toLocalDate().isEqual(startOfWeek) || p.getCreateDate().toLocalDate().isAfter(startOfWeek))
                    .mapToLong(Payment::getPrice)
                    .sum();

            // 요일 별로 합산 -> Map<요일, 요일 별 매출>
            graph = payments.stream()
                    .filter(p -> p.getCreateDate().toLocalDate().isEqual(startOfWeek) || p.getCreateDate().toLocalDate().isAfter(startOfWeek))
                    .collect(Collectors.groupingBy(
                            p -> p.getCreateDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN), // 요일 기준 그룹화
                            Collectors.summingLong(Payment::getPrice) // 가격 합산
                    ));
        } else if (period.equals(Period.ONE_MONTH)) { // 이번 달인 경우 -> ONE_MONTH
            LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()); // 현재 날짜가 소속된 달의 첫번째 날짜 가져오기

            // 현재 날짜가 소속된 달의 총 금액 계산
            totalAmount = payments.stream()
                    .filter(p -> p.getCreateDate().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                    .mapToLong(Payment::getPrice)
                    .sum();

            // 날짜 별로 합산 -> Map<날짜, 날짜 별 매출>
            graph = payments.stream()
                    .filter(p -> p.getCreateDate().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                    .collect(Collectors.groupingBy(
                            p -> String.valueOf(p.getCreateDate().getDayOfMonth()) + "일", // 날짜 기준 그룹화
                            Collectors.summingLong(Payment::getPrice) // 가격 합산
                    ));
        } else if (period.equals(Period.ONE_YEAR)) { // 이번 해인 경우 -> ONE_YEAR
            LocalDate startOfYear = now.with(TemporalAdjusters.firstDayOfYear()); // 현재 날짜가 소속된 년도의 첫번째 날짜 가져오기

            // 현재 날짜가 소속된 년도의 총 금액 계산
            totalAmount = payments.stream()
                    .filter(p -> p.getCreateDate().toLocalDate().isEqual(startOfYear) || p.getCreateDate().toLocalDate().isAfter(startOfYear))
                    .mapToLong(Payment::getPrice)
                    .sum();

            // 날짜 별로 합산 -> Map<년도, 년도 별 매출>
            graph = payments.stream()
                    .filter(p -> p.getCreateDate().toLocalDate().isEqual(startOfYear) || p.getCreateDate().toLocalDate().isAfter(startOfYear))
                    .collect(Collectors.groupingBy(
                            p -> p.getCreateDate().getMonth().getDisplayName(TextStyle.FULL, Locale.KOREAN), // 월 기준 그룹화
                            Collectors.summingLong(Payment::getPrice) // 가격 합산
                    ));
        }


        // 오늘 매출 총 금액 계산
        long todayTotalAmount;
        todayTotalAmount = payments.stream()
                .filter(p -> p.getCreateDate().toLocalDate().isEqual(LocalDate.now()))
                .mapToLong(Payment::getPrice)
                .sum();

        // DTO에 로직 결과를 담아 반환
        return ShopSalesResponseDto.builder()
                .totalAmount(totalAmount)
                .todayTotalAmount(todayTotalAmount)
                .graph(graph)
                .build();
    }

    /**
     * 사업자 소속 디자이너별 매출 조회
     * @return 소속 디자이너별 매출 정보를 담은 DTO
     */
    public List<DesignerSalesResponseDto> getDesignersSales() {
        // 로그인 정보에서 가게 이메일을 꺼내고, 가게 조회
        String shopEmail = getAuthenticatedEmail();
        Shop shop = getShop(shopEmail);

        // 가게의 결제가 성공상태이며, 취소되지 않은 것들만 필터링
        List<Payment> payments = shop.getPayments().stream()
                .filter(p -> p.getPaySuccessYN() && p.getCancelYN() == null)
                .toList();

        // 이번 달 기준에 따라 필터링한 뒤, 디자이너 별로 그룹화한 총 금액 계산
        LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        Map<Designer, Long> groupingSales = payments.stream()
                .filter(p -> p.getCreateDate().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                .collect(Collectors.groupingBy(Payment::getDesigner, Collectors.summingLong(Payment::getPrice)));

        // DTO 반환
        return groupingSales.entrySet().stream()
//                .map(e -> new DesignerSalesResponseDto(e.getKey().getName(), e.getKey().getEmail(), e.getValue(), e.getKey().getImage()))
                .map(e -> DesignerSalesResponseDto.builder()
                        .designerName(e.getKey().getName())
                        .designerEmail(e.getKey().getEmail())
                        .designerSales(e.getValue())
                        .designerImage(e.getKey().getImage())
                        .build())
                .toList();
    }

    /**
     * 사업자 소속 디자이너 중 단건의 매출 조회(캘린더)
     * @param designerEmail 매출 조회할 디자이너의 이메일
     * @return 날짜, 날짜의매출 형식의 맵 반환
     */
    public Map<Integer, Long> getDesignerSales(String designerEmail, Integer year, Integer month) {
        Designer designer = getDesigner(designerEmail);

        // 결제가 성공상태이며, 취소되지 않은 것들만 필터링
        List<Payment> filteredPayments = designer.getPayments().stream()
                .filter(p -> p.getPaySuccessYN() && p.getCancelYN() == null)
                .toList();

        // 해당 연월의 결제 건들을 피터링
        LocalDate startOfMonth = LocalDate.of(year, month, 1); // 해당 월의 시작 날짜
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth()); // 해당 월의 마지막 날짜

        List<Payment> payments = filteredPayments.stream()
                .filter(p -> {
                    LocalDate paymentDate = p.getCreateDate().toLocalDate();
                    return paymentDate.isAfter(startOfMonth.minusDays(1)) && !paymentDate.isAfter(endOfMonth.plusDays(1));
                })
                .toList();


        return payments.stream()
                .collect(Collectors.toMap(
                        p -> p.getCreateDate().getDayOfMonth(),
                        Payment::getPrice,
                        Long::sum // 동일한 날짜가 있을 경우 가격 합산
                ));
    }

    /**
     * 사업자 소속 디자이너 중 단건의 매출 조회(날짜)
     * @param designerEmail 매출 조회할 디자이너의 이메일
     * @return 날짜, 날짜의매출 형식의 맵 반환
     */
    public List<DesignerSalesDetailResponseDto> getDesignerSale(String designerEmail, Integer year, Integer month, Integer day) {
        Designer designer = getDesigner(designerEmail);

        // 결제가 성공상태이며, 취소되지 않은 것들만 필터링
        List<Payment> filteredPayments = designer.getPayments().stream()
                .filter(p -> p.getPaySuccessYN() && p.getCancelYN() == null)
                .toList();

        return filteredPayments.stream()
                .filter(p -> p.getCreateDate().toLocalDate().equals(LocalDate.of(year, month, day)))
                .map( p -> DesignerSalesDetailResponseDto.builder()
                        .paymentTime(p.getCreateDate())
                        .menuName(p.getReservMenuName())
                        .sales(p.getPrice())
                        .userName(p.getUser().getName())
                        .build())
                .toList();
    }

    /**
     * 사업자 이번 달의 매출 우수 디자이너
     * @return 우수 디자이너의 관련 정보를 담은 DTO
     */
    public DesignerSalesResponseDto getBestSalesDesigner() {
        // 로그인 정보에서 가게 이메일을 꺼내고, 가게 조회
        String shopEmail = getAuthenticatedEmail();
        Shop shop = getShop(shopEmail);

        // 가게의 결제가 성공상태이며, 취소되지 않은 것들만 필터링
        List<Payment> payments = shop.getPayments().stream()
                .filter(p -> p.getPaySuccessYN() && p.getCancelYN() == null)
                .toList();

        // 이번 달 기준에 따라 필터링한 뒤, 디자이너 별로 그룹화한 총 금액 계산
        LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        Map<Designer, Long> groupingSales = payments.stream()
                .filter(p -> p.getCreateDate().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                .collect(Collectors.groupingBy(Payment::getDesigner, Collectors.summingLong(Payment::getPrice)));

        // 최고 매출 디자이너 찾기
        return shop.getPayments().stream()
                .filter(p -> p.getPaySuccessYN() && (p.getCancelYN() == null || !p.getCancelYN()))
                .filter(p -> p.getCreateDate().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                .collect(Collectors.groupingBy(Payment::getDesigner, Collectors.summingLong(Payment::getPrice)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> DesignerSalesResponseDto.builder()
                        .designerName(entry.getKey().getName())
                        .designerEmail(entry.getKey().getEmail())
                        .designerSales(entry.getValue())
                        .designerImage(entry.getKey().getImage())
                        .build())
                .orElse(DesignerSalesResponseDto.builder()
                        .designerName("")
                        .designerEmail("")
                        .designerSales(0L)
                        .designerImage("")
                        .build());
    }

    /**
     * 이번달 좋아요 수가 가장 많이 증가한 디자이너 조회
     * @return
     */
    public DesignerLikeResponseDto getBestLikeDesinger() {
        Shop shop = getShop(getAuthenticatedEmail());
        List<Designer> designers = shop.getDesigners();

        LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());

        return designers.stream()// 디자이너 별 스트림
                .map(designer -> { // map을 통해 designer -> DTO로 변환
                    long likeCountThisMonth = designer.getUserDesignerLikes().stream() // 디자이너 별 좋아요로 스트림
                            .filter(like -> like.getCreatedDate().isAfter(startOfMonth.minusDays(1))) // 이번 달의 좋아요한 날짜를 필터링
                            .count(); // 필터링한 좋아요 개수 계산
                    return new DesignerLikeResponseDto(designer.getName(), designer.getEmail(), likeCountThisMonth, designer.getImage()); // -> DTO
                })
                .max(Comparator.comparingLong(DesignerLikeResponseDto::getIncreasedLikes)) // 가장 많이 증가한 디자이너 찾기
                .orElse(null);

    }

    /**
     * 시큐리티 인증 정보에서 이메일 가져오기
     * 현재 인증된 사용자의 이메일 반환
     *
     * @return 인증된 사용자 이메일
     */
    public static String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal = (UserDetailsDto) authentication.getPrincipal();
        return principal.getUsername();
    }

    /**
     * 사업자 이메일로 사업자 조회
     * 특정 이메일로 사업자 정보 반환
     *
     * @param email 사업자의 이메일
     * @return 찾은 사업자 정보
     */
    private Shop getShop(String email) {
        return shopRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다."));
    }

    /**
     * 유저 이메일로 유저 조회
     * 특정 이메일로 사용자 정보 반환
     *
     * @param email 유저의 이메일
     * @return 찾은 유저 정보
     */
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
    }

    /**
     * 디자이너 이메일로 디자이너 조회
     * 특정 이메일로 디자이너 정보 반환
     *
     * @param email 디자이너의 이메일
     * @return 찾은 디자이너 정보
     */
    private Designer getDesigner(String email) {
        return designerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 디자이너를 찾을 수 없습니다."));
    }

    /**
     * 사업자 이름 조회 (헤더 반환)
     */
    public String loadHeader() {
        return getShop(getAuthenticatedEmail()).getName(); // 시큐리티 인증정보에서 꺼낸 이메일 조회 -> 가게 조회 -> 가게 이름 조회
    }
}
