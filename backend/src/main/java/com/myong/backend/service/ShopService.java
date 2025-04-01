package com.myong.backend.service;

import com.myong.backend.api.KakaoMapApi;
import com.myong.backend.domain.dto.shop.ShopNoticeRequest;
import com.myong.backend.domain.dto.coupon.CouponListResponseDto;
import com.myong.backend.domain.dto.coupon.CouponRegisterRequestDto;
import com.myong.backend.domain.dto.event.EventListResponseDto;
import com.myong.backend.domain.dto.event.EventRegisterRequestDto;
import com.myong.backend.domain.dto.job.JobPostEditDto;
import com.myong.backend.domain.dto.job.JobPostListResponseDto;
import com.myong.backend.domain.dto.menu.MenuEditDto;
import com.myong.backend.domain.dto.menu.MenuListResponseDto;
import com.myong.backend.domain.dto.reservation.request.ShopReservationRequestDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationDetailResponseDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationResponseDto;
import com.myong.backend.domain.dto.shop.*;
import com.myong.backend.domain.entity.Gender;
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
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.repository.*;
import com.myong.backend.repository.mybatis.AttendanceMapper;
import com.myong.backend.repository.mybatis.ReservationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final ShopRepository shopRepository;
    private final DefaultMessageService messageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CouponRepository couponRepository;
    private final EventRepository eventRepository;
    private final MenuRepository menuRepository;
    private final DesignerRepository designerRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
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

    /**
     * 사업자 이메일 중복 확인
     * 이메일을 사용 중인지 확인하고 결과를 반환
     *
     * @param email 중복 확인할 이메일
     * @return 이메일 사용 가능 여부 메시지
     * @throws ExistSameEmailException 이미 사용 중인 이메일일 때 발생
     */
    public String checkEmail(String email) {
        Optional<Shop> findShop = shopRepository.findByEmail(email); // 이메일로 가게 찾기

        if (findShop.isEmpty()) return "사용가능한 이메일입니다."; //null이면 사용가능한 이메일
        else throw new ExistSameEmailException("이미 사용중인 이메일 입니다."); // 이미 있으면 예외 던지기
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
        Shop signedShop = shopRepository.save(shop);

        // 기본 공통항목 생성
        addCommon(signedShop);
        return "사업자 회원가입에 성공했습니다.";
    }


    /**
     * 사업자 쿠폰 등록
     * 쿠폰 정보를 저장
     *
     * @param request 쿠폰 정보가 담긴 DTO
     * @return 쿠폰 등록 결과 메시지
     */
    public String addCoupon(CouponRegisterRequestDto request) {
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
    public List<CouponListResponseDto> getCoupons() {
        // 인증 정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);

        List<Coupon> coupons = couponRepository.findByShop(shop);// 가게를 통해 가져온 쿠폰들 반환
        List<CouponListResponseDto> response = new ArrayList<>(); // 쿠폰 목록 리스트 생성
        for (Coupon coupon : coupons) { // 쿠폰 목록에 쿠폰 담기
            CouponListResponseDto couponListResponseDto = new CouponListResponseDto(
                    coupon.getId().toString(),
                    coupon.getName(),
                    coupon.getType().toString(),
                    coupon.getPrice(),
                    coupon.getGetDate(),
                    coupon.getUseDate()
            );
            response.add(couponListResponseDto);
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
    public String addEvent(EventRegisterRequestDto request) {
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
    public List<EventListResponseDto> getEvents() {
        // 로그인 인증 정보에서 이메일 가져오기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);

        List<Event> events = eventRepository.findByShop(shop);// 가게를 통해 가져온 이벤트들 반환
        List<EventListResponseDto> response = new ArrayList<>(); // 이벤트 목록 리스트 생성
        for (Event event : events) { // 이벤트 목록에 이벤트 담기
            EventListResponseDto eventListResponseDto = new EventListResponseDto(
                    event.getId().toString(),
                    event.getName(),
                    event.getPrice(),
                    event.getType().toString(),
                    event.getStartDate().toString(),
                    event.getEndDate().toString()
            );
            response.add(eventListResponseDto);
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
        return new ShopProfileResponseDto(
                shop.getName(),
                shop.getAddress(),
                shop.getPost(),
                shop.getTel(),
                shop.getPwd(),
                shop.getDesc(),
                shop.getOpenTime().toString(),
                shop.getCloseTime().toString(),
                shop.getRegularHoliday()
        );
    }

    /**
     * 사업자 프로필 정보 수정
     * 기존 프로필 정보를 수정
     *
     * @param request 프로필 수정 요청 정보가 담긴 DTO
     * @return 프로필 수정 결과 메시지
     */
    public String updateProflie(ShopProfileRequestDto request) {
        // 인증정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);
        shop.updateProfile(request); // 찾은 가게의 프로필 정보 수정
        return "프로필이 수정되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 메뉴 추가
     * 새로운 메뉴를 등록
     *
     * @param request 메뉴 등록 요청 정보가 담긴 DTO
     * @return 메뉴 등록 결과 메시지
     */
    public String addMenu(@Valid MenuEditDto request) {
        // 인증정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);

        // 디자이너 이메일로 디자이너 찾기
        Designer designer = getDesigner(request.getDesignerEmail());

        // 메뉴 엔티티 개체 생성 후 저장
        Menu menu = Menu.builder()
                .name(request.getName())
                .desc(request.getDesc())
                .price(request.getPrice())
                .estimatedTime(request.getEstimatedTime())
                .common(request.getCommon())
                .shop(shop)
                .designer(designer)
                .build();

        menuRepository.save(menu);
        return "성공적으로 메뉴가 등록되었습니다.";
    }

    /**
     * 사업자 메뉴 조회
     * 등록된 메뉴 목록 반환
     *
     * @return 메뉴 목록
     */
    public List<MenuListResponseDto> getMenu() {
        // 로그인 인증 정보에서 이메일 가져오기
        String email = getAuthenticatedEmail();

        Shop shop = shopRepository.findByEmail(email)
                .orElseThrow(() ->  new NoSuchElementException("가게를 찾을 수 없습니다.")); // 가게 이메일로 가게 찾기


        List<Menu> menus = menuRepository.findByShop(shop);// 가게의 메뉴 찾기
        List<MenuListResponseDto> response = new ArrayList<>(); // 메뉴 목록 리스트 생성
        for (Menu menu : menus) { // 메뉴 목록에 메뉴 담기
            MenuListResponseDto menuListResponseDto = new MenuListResponseDto(
                    menu.getId().toString(),
                    menu.getName(),
                    menu.getDesigner().getName(),
                    menu.getPrice()
            );
            response.add(menuListResponseDto);
        }
        return response; // 메뉴 목록 반환
    }

    /**
     * 사업자 메뉴 수정
     * 기존 메뉴 정보 수정
     *
     * @param request 메뉴 수정 요청 정보가 담긴 DTO
     * @return 메뉴 수정 결과 메시지
     */
    public String updateMenu(@Valid MenuEditDto request) {
        Menu menu = menuRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new NoSuchElementException("해당 메뉴를 찾을 수 없습니다.")); // 메뉴 이이디로 찾기
        menu.edit(request); // 편의 메서드로 메뉴 정보 수정

        return "성공적으로 메뉴가 수정되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 메뉴 삭제
     * 메뉴 정보 삭제
     *
     * @param request 메뉴 삭제 요청 정보가 담긴 DTO
     * @return 메뉴 삭제 결과 메시지
     */
    public String deleteMenu(@Valid MenuEditDto request) {
        Menu menu = menuRepository.findById(UUID.fromString(request.getId()))
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
    public String addJobPost(JobPostEditDto request) {
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
    public List<JobPostListResponseDto> getJobPosts() {
        // 로그인 인증 정보에서 이메일 가져오기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);
        List<JobPost> jobPosts = jobPostRepository.findByShop(shop.getId());// 가게의 고유 키를 통해 가져온 구인글 목록 반환

        List<JobPostListResponseDto> response = new ArrayList<>(); // 구인글 목록 리스트 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd"); // 날짜 포매터 만들기
        for (JobPost jobPost : jobPosts) { // 구인글 목록에 구인글 목록 담기
            JobPostListResponseDto jobPostListResponseDto = new JobPostListResponseDto(
                    jobPost.getShop().getName(),
                    jobPost.getId().toString(),
                    jobPost.getTitle(),
                    jobPost.getSalary(),
                    jobPost.getGender().toString(),
                    jobPost.getWork().toString(),
                    jobPost.getWorkTime().toString(),
                    jobPost.getLeaveTime().toString(),
                    jobPost.getContent()
            );
            response.add(jobPostListResponseDto); // 구인글 목록 dto 반환
        }
        return response;
    }

    /**
     * 사업자 구인글 수정
     * 기존 구인글 정보 수정
     *
     * @param request 구인글 수정 요청 정보가 담긴 DTO
     * @return 구인글 수정 결과 메시지
     */
    public String updateJobPost(JobPostEditDto request) {// 가게 찾기
        JobPost jobPost = jobPostRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new NoSuchElementException("해당 구인글을 찾을 수 없습니다.")); // 구인글 아이디로 구인글 찾기
        jobPost.updateJobPost(request); // 구인글 수정 편의 메서드를 통해 수정
        return "성공적으로 구인글이 수정되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 구인글 삭제
     * 구인글 정보 삭제
     *
     * @param request 구인글 삭제 요청 정보가 담긴 DTO
     * @return 구인글 삭제 결과 메시지
     */
    public String deleteJobPost(JobPostEditDto request) {
        JobPost jobPost = jobPostRepository.findById(UUID.fromString(request.getId()))
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


        return "성공적으로 디자이너가 추가되었습니다.";
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
    public List<ShopDesignerListResponseDto> getDesigners() {
        // 인증 정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        Shop shop = getShop(email);

        List<Designer> designers = shop.getDesigners();// 디자이너들 가져오기
        List<ShopDesignerListResponseDto> dtos = new ArrayList<>();
        for (Designer designer : designers) { // 가져온 디자이너들의 정보를 dto에 담은 뒤 리스트로 반환
            ShopDesignerListResponseDto dto = ShopDesignerListResponseDto.builder()
                    .email(designer.getEmail())
                    .name(designer.getName())
                    .like(designer.getLike())
                    .gender(designer.getGender().toString())
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
    public String deleteDesigner(ShopDesignerRequestDto request) {
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
        blackListRepository.findByShopAndUser(shop,user)
                .orElseThrow(() -> new RuntimeException("이미 블랙리스트에 추가된 유저입니다."));

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
     * @return 블랙리스트 목록
     */
    public List<BlackListResponseDto> getBlackList(String id) {
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
                    .reason(blackList.getReason())
                    .userName(blackList.getUser().getName())
                    .userEmail(blackList.getUser().getEmail())
                    .build();
            dtos.add(dto);
        }

        return dtos;
    }

    /**
     * 사업자 블랙리스트 삭제
     * 블랙리스트에서 특정 사용자 정보 삭제
     *
     * @param requests 블랙리스트 삭제 요청 정보가 담긴 DTO 리스트
     * @return 블랙리스트 삭제 결과 메시지
     */
    public String deleteBlackList(List<BlackListRequestDto> requests) {
        for (BlackListRequestDto request : requests) {
            // 인증 정보에서 사업자 이메일 꺼내기
            String email = getAuthenticatedEmail();

            // 가게 조회
            Shop shop = getShop(email);

            // 유저 조회
            User user = getUser(request.getUserEmail());

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
    public List<ShopReservationResponseDto> getReservations(ShopReservationRequestDto request) {
        // 인증 정보에서 사업자 이메일 꺼내기
        String email = getAuthenticatedEmail();

        // 가게 찾기
        getShop(email);
        // MyBatis SqlMapper를 통해 예약 조회하기
        return reservationMapper.findAll(request);
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
    public String createNotice(ShopNoticeRequest request) {
        String email = getAuthenticatedEmail(); // 로그인 인증 정보에서 이메일 꺼내기
        Shop shop = getShop(email); // 꺼낸 이메일 -> 가게 조회

        Notice notice = Notice.builder() // 공지사항 객체 생성 후 -> 저장(영속성 컨텍스트)
                .title(request.getTitle())
                .content(request.getContent())
                .shop(shop)
                .build();
        noticeRepository.save(notice);

        return "공지사항이 성공적으로 생성되었습니다.";
    }

    /**
     * 공지사항 전체 조회
     * 현재 로그인한 사업자의 이메일을 가지고 전체 공지사항 개체 조회
     * @return 공지사항 정보를 담은 DTO들을 반환
     */
    public List<ShopNoticeResponse> getNotices() {
        String email = getAuthenticatedEmail(); // 로그인 인증 정보에서 이메일 꺼내기
        Shop shop = getShop(email); // 꺼낸 이메일 -> 가게 조회

        List<Notice> notices = noticeRepository.findByShop(shop); // 조회한 가게 객체를 통해 공지사항 조회

        // 스트림을 통해 Notice -> DTO 객체로 map 중간연산을 통해 변환한 뒤 반환
        return notices.stream()
                .map((n) -> ShopNoticeResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .createDate(n.getCreateDate())
                        .build())
                .toList();
    }

    /**
     * 공지사항 단건 조회
     * 전체 공지사항 개체 중 단건 조회
     * @param id 조회하고자 하는 공지사항 개체의 고유 키
     * @return 공지사항 상세정보를 담은 DTO를 반환
     */
    public ShopNoticeDetailResponse getNotice(String id) {
        Notice notice = noticeRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("찾고자 하는 공지사항 글이 없습니다."));

        return ShopNoticeDetailResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createDate(notice.getCreateDate())
                .build();
    }

    /**
     * 가장 최신의 공지사항 단건 조회
     * 현재 로그인한 사업자의 이메일을 가지고 전체 공지사항 중 가장 최신의 공지사항 개체 조회
     * @return 공지사항 상세정보를 담은 DTO를 반환
     */
    public ShopNoticeDetailResponse getNoticeLatest() {
        String email = getAuthenticatedEmail(); // 로그인 인증 정보에서 이메일 꺼내기
        Shop shop = getShop(email); // 꺼낸 이메일 -> 가게 조회

        List<Notice> notices = noticeRepository.findByShop(shop); // 조회한 가게 객체를 통해 공지사항 조회
        
        // 스트림을 통해 Notice 리스트 -> sorted 중간 연산을 통해 생성일 내림차순으로 정렬 -> 첫번째 항목 찾기
        Notice notice = notices.stream()
                .sorted(Comparator.comparing(Notice::getCreateDate).reversed())
                .findFirst()
                .orElse(null);

        // 찾은 Notice -> DTO로 반환
        return ShopNoticeDetailResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createDate(notice.getCreateDate())
                .build();
    }

    /**
     * 공지사항 단건 수정
     * 현재 로그인한 사업자의 이메일을 가지고 전체 공지사항 중 가장 최신의 공지사항 개체 조회
     * @param id 조회하고자 하는 공지사항 개체의 고유 키
     * @param request 공지사항 개체 수정에 필요한 정보가 담긴 DTO
     * @return 성공 구문 반환
     */
    public String updateNotice(String id, ShopNoticeRequest request) {
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
     * 시큐리티 인증 정보에서 이메일 가져오기
     * 현재 인증된 사용자의 이메일 반환
     *
     * @return 인증된 사용자 이메일
     */
    private static String getAuthenticatedEmail() {
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
     * 사업자 공통 항목 추가
     * 사업자가 회원가입 시 추후 메뉴에서 활용할 공통항목 추가
     */
    private void addCommon(Shop shop) {
        // 공통 항목 생성 후 저장(디자이너 로직 생성 로직과 유사)
        Designer common = Designer.builder()
                .name("공통")
                .nickName("공통")
                .email(UUID.randomUUID().toString() + "@dummy.com") // 무작위의 이메일 지정
                .password(UUID.randomUUID().toString()) // 무작위의 비밀번호 지정 -> 향후 디자이너로 로그인 시도 등 악용 방지
                .tel("")
                .birth(LocalDate.of(9999, 12, 31)) // 9999-12-3
                .gender(Gender.NO)
                .like(0)
                .rating(0.0)
                .totalRating(0.0)
                .reviewCount(0)
                .workTime(LocalTime.of(0,0))
                .leaveTime(LocalTime.of(0,0))
                .build();

        designerRepository.save(common);

        // 회원가입된 가게에 공통 항목 추가
        common.getJob(shop);
    }

}
