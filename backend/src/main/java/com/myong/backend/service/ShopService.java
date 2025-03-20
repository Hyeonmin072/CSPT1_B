package com.myong.backend.service;

import com.myong.backend.api.KakaoMapApi;
import com.myong.backend.domain.dto.coupon.CouponListResponseDto;
import com.myong.backend.domain.dto.coupon.CouponRegisterRequestDto;
import com.myong.backend.domain.dto.event.EventListResponseDto;
import com.myong.backend.domain.dto.event.EventRegisterRequestDto;
import com.myong.backend.domain.dto.job.JobPostEditDto;
import com.myong.backend.domain.dto.job.JobPostListResponseDto;
import com.myong.backend.domain.dto.menu.MenuEditDto;
import com.myong.backend.domain.dto.menu.MenuListResponseDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationDetailResponseDto;
import com.myong.backend.domain.dto.reservation.request.ShopReservationRequestDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationResponseDto;
import com.myong.backend.domain.dto.shop.*;
import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.*;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.DiscountType;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.usershop.BlackList;
import com.myong.backend.exception.ExistSameEmailException;
import com.myong.backend.exception.NotEqualVerifyCodeException;
import com.myong.backend.repository.*;
import com.myong.backend.repository.mybatis.ReservationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
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



    /**
     * 사업자 회원가입 로직
     * @param request
     * @return
     */
    public String shopSignUp(ShopSignUpRequestDto request) {
        String result = kakaoMapApi.getCoordinatesFromAddress(request.getAddress());
        System.out.println("위도와 경도:"+result);
        String latitude = result.split(" ")[0];
        String longitude = result.split(" ")[1];

            Shop shop = new Shop( // 가게 생성
                    request.getName(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getAddress(),
                    request.getTel(),
                    request.getBizId(),
                    request.getPost(),
                    Double.parseDouble(longitude),
                    Double.parseDouble(latitude)
            );
            shopRepository.save(shop); // 가게 저장
            return "사업자 회원가입에 성공했습니다.";
    }


    /**
     * 사업자 전화번호 인증코드 보내기 로직
     * @param request
     * @return
     */
    public SingleMessageSentResponse sendOne(ShopTelRequestDto request) {
        Message message = new Message(); // 메시지 객체 생성(외부 라이브러리에서 가져옴)
        message.setFrom("01033791271"); // 보낼 전화번호
        message.setTo(request.getTel()); // 받을 전화번호

        Random random = new Random();
        int verifyCode = 100000 + random.nextInt(900000); // 100000 ~ 999999사이 랜덤 코드 생성
        message.setText("[Hairism] 인증코드를 입력해주세요 : " + verifyCode); // 메시지 내용 설정

        redisTemplate.opsForValue().set(request.getTel(), verifyCode, 5, TimeUnit.MINUTES); // redis에 키, 값 각각 전화번호, 인증번호 형태로 저장, 5분 시간제한
        return messageService.sendOne(new SingleMessageSendingRequest(message)); // 보내고 난 후 response 반환
    }


    /**
     * 사업자 전화번호 인증코드 확인 로직
     * @param request
     * @return
     */
    public String checkVerifyCode(ShopVerifyCodeRequestDto request) {
        Integer verifyCode = (Integer) redisTemplate.opsForValue().get(request.getTel());// redis에서 키 값으로 인증번호 꺼내기

        if(verifyCode.equals(request.getVerifyCode())) return "인증이 완료되었습니다."; // request의 인증코드가 redis에 저장된 값이랑 같을때
        else throw new NotEqualVerifyCodeException("인증코드가 일치하지 않습니다."); // request의 인증코드가 redis에 저장된 값이랑 인증코드가 다르면 예외 던지기
    }


    /**
     * 사업자번호 인증 로직
     * @param request
     * @return
     */
    public String checkBiz(ShopBizRequestDto request) {
        return "사업자 정보가 확인되었습니다.";
    }

    /**
     * 사업자 이메일 중복확인 로직
     * @param request
     * @return
     */
    public String checkEmail(ShopEmailRequestDto request) {
        Optional<Shop> findShop = shopRepository.findByEmail(request.getEmail()); // 이메일로 가게 찾기

        if (!findShop.isPresent()) return "사용가능한 이메일입니다."; //null이면 사용가능한 이메일
        else throw new ExistSameEmailException("이미 사용중인 이메일 입니다."); // 이미 있으면 예외 던지기
    }

    /**
     * 사업자 쿠폰 조회 로직
     * @param request
     * @return
     */
    public List<CouponListResponseDto> getCoupons(ShopEmailRequestDto request) {
        Shop shop = shopRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 이메일로 가게 찾기

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
     * 사업자 쿠폰 등록 로직
     * @param request
     * @return
     */
    public String addCoupon(CouponRegisterRequestDto request) {
        Optional<Shop> findShop = shopRepository.findByEmail(request.getShopEmail()); // 이메일로 가게 찾기
        if(!findShop.isPresent()){
            throw new NoSuchElementException("해당 가게를 찾지못했습니다");
        }
        Shop shop = findShop.get();

        Coupon coupon = new Coupon( // 쿠폰 생성
                request.getName(),
                DiscountType.valueOf(request.getType()),
                request.getPrice(),
                Period.ofDays(request.getGetDate()),
                Period.ofDays(request.getUseDate()),
                shop
        );
        couponRepository.save(coupon); // 쿠폰 저장

        return "성공적으로 쿠폰이 등록되었습니다."; // 로직 수행결과 반환
    }

    /**
     * 사업자 이벤트 조회 로직
     * @param request
     * @return
     */
    public List<EventListResponseDto> getEvents(ShopEmailRequestDto request) {
        Shop shop = shopRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 이메일로 가게 찾기

        List<Event> events = eventRepository.findByShop(shop);// 가게를 통해 가져온 이벤트들 반환
        List<EventListResponseDto> response = new ArrayList<>(); // 이벤트 목록 리스트 생성
        for (Event event : events) { // 이벤트 목록에 이벤트 담기
            EventListResponseDto eventListResponseDto = new EventListResponseDto(
                    event.getId().toString(),
                    event.getName(),
                    event.getAmount(),
                    event.getType().toString(),
                    event.getStartDate().toString(),
                    event.getEndDate().toString()
            );
            response.add(eventListResponseDto);
        }
        return response; // 이벤트 목록 반환
    }

    /**
     * 사업자 이벤트 등록 로직
     * @param request
     * @return
     */
    public String addEvent(EventRegisterRequestDto request) {
        Shop shop = shopRepository.findByEmail(request.getShopEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다."));// 이메일로 가게 찾기

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd"); // 날짜 포매터 만들기
        Event event = new Event( // 이벤트 생성
                request.getName(),
                request.getAmount(),
                DiscountType.valueOf(request.getType()),
                LocalDate.parse(request.getStartDate(), formatter), // YYYY-MM-DD 형식으로 저장
                LocalDate.parse(request.getEndDate(), formatter), // YYYY-MM-DD 형식으로 저장
                shop
        );
        eventRepository.save(event); // 이벤트 저장

        return "성공적으로 이벤트가 등록되었습니다."; // 로직 수행결과 반환
    }

    /**
     * 사업자 프로필 정보 조회
     * @param request
     * @return
     */ 
    public ShopProfileResponseDto getProfile(ShopEmailRequestDto request) {
        Shop shop = shopRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 이메일로 가게 찾기
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
     * @param request
     * @return
     */
    public String updateProflie(ShopProfileRequestDto request) {
        Shop shop = shopRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 이메일로 가게 찾기
        shop.updateProfile(request); // 찾은 가게의 프로필 정보 수정
        return "프로필이 수정되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 메뉴 조회
     * @param request
     * @return
     */
    public List<MenuListResponseDto> getMenu(@Valid ShopEmailRequestDto request) {
        Shop shop = shopRepository.findByEmail(request.getEmail())
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
     * 사업자 메뉴 추가
     * @param request
     * @return
     */
    public String addMenu(@Valid MenuEditDto request) {
        Shop shop = shopRepository.findByEmail(request.getShopEmail())
                .orElseThrow(() ->  new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 가게 이메일로 가게 찾기
        Designer designer = designerRepository.findByEmail(request.getDesignerEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 디자이너를 찾을 수 없습니다.")); // 디자이너 이메일로 디자이너 찾기
        Menu menu = Menu.builder() // 메뉴 엔티티 개체 생성
                .name(request.getName())
                .desc(request.getDesc())
                .price(request.getPrice())
                .estimatedTime(request.getEstimatedTime())
                .common(request.getCommon())
                .shop(shop)
                .designer(designer)
                .build();

        menuRepository.save(menu); // 메뉴를 영속성 컨텍스트에 저장
        return "성공적으로 메뉴가 등록되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 메뉴 수정
     * @param request
     * @return
     */
    public String updateMenu(@Valid MenuEditDto request) {
        Menu menu = menuRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new NoSuchElementException("해당 메뉴를 찾을 수 없습니다.")); // 메뉴 이이디로 찾기
        menu.edit(request); // 편의 메서드로 메뉴 정보 수정

        return "성공적으로 메뉴가 수정되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 메뉴 삭제
     * @param request
     * @return
     */
    public String deleteMenu(@Valid MenuEditDto request) {
        Menu menu = menuRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new NoSuchElementException("해당 메뉴를 찾을 수 없습니다.")); // 메뉴 이이디로 찾기
        menuRepository.delete(menu); // 메뉴 삭제
        return "성공적으로 메뉴가 삭제되었습니다."; // 성공 구문 반환
    }




    /**
     * 사업자 구인글 목록 조회
     * @param request
     * @return
     */
    public List<JobPostListResponseDto> getJobPosts(ShopEmailRequestDto request) {
        Shop shop = shopRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 이메일로 가게 찾기
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
     * 사업자 구인글 등록
     * @param request
     * @return
     */
    public String addJobPost(JobPostEditDto request) {
        Shop shop = shopRepository.findByEmail(request.getShopEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 구인글이 등록될 가게 찾기

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
     * 사업자 구인글 수정
     * @param request
     * @return
     */
    public String updateJobPost(JobPostEditDto request) {// 가게 찾기
        JobPost jobPost = jobPostRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new NoSuchElementException("해당 구인글을 찾을 수 없습니다.")); // 구인글 아이디로 구인글 찾기
        jobPost.updateJobPost(request); // 구인글 수정 편의 메서드를 통해 수정
        return "성공적으로 구인글이 수정되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 구인글 삭제
     * @param request
     * @return
     */
    public String deleteJobPost(JobPostEditDto request) {
        JobPost jobPost = jobPostRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new NoSuchElementException("해당 구인글을 찾을 수 없습니다.")); // 구인글 아이디로 구인글 찾기
        jobPostRepository.delete(jobPost); // 구인글 삭제
        return "성공적으로 구인글이 마감되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 소속된 디자이너 목록 조회
     * @param request
     * @return
     */
    public List<ShopDesignerListResponseDto> getDesigners(ShopEmailRequestDto request) {
        Shop shop = shopRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 찾을 디자이너 찾기
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
     * 사업자 디자이너 추가
     * @param request
     * @return
     */
    public String joinDesigner(ShopDesignerRequestDto request) {
        Designer designer = designerRepository.findByEmail(request.getDesignerEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 디자이너를 찾을 수 없습니다.")); // 가입될 디자이너 찾기

        Shop shop = shopRepository.findByEmail(request.getShopEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 디자이너가 가입될 가게 찾기

        designer.getJob(shop); // 둘 다 찾았다면, 가게에 디자이너 추가

        return "성공적으로 디자이너가 추가되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 디자이너 삭제
     * @param request
     * @return
     */
    public String deleteDesigner(ShopDesignerRequestDto request) {
        Designer designer = designerRepository.findByEmail(request.getDesignerEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 디자이너를 찾을 수 없습니다.")); // 삭제될 디자이너 찾기

        Shop shop = shopRepository.findByEmail(request.getShopEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 디자이너가 삭제될 가게 찾기

        designer.fire(); // 둘 다 찾았다면, 가게에서 디자이너 삭제

        return "성공적으로 디자이너가 삭제되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 블랙리스트 목록 조회
     * @param request
     * @return
     */
    public List<BlackListResponseDto> getBlackLists(ShopEmailRequestDto request) {
        Shop shop = shopRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 가게 찾기

        List<BlackList> blackLists = blackListRepository.findByShop(shop); // 가게의 블랙리스트 페치 조인으로 조회
        List<BlackListResponseDto> dtos = new ArrayList<>(); 
        for (BlackList blackList : blackLists) { // 가져온 블랙리스트들을 dto에 담은 뒤 리스트로 반환
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
     * 사업자 블랙리스트 추가
     * @param request
     * @return
     */
    public String createBlackList(BlackListRequestDto request) {
        Shop shop = shopRepository.findByEmail(request.getShopEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다.")); // 가게 찾기

        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다.")); // 유저 찾기

        if(blackListRepository.findByShopAndUser(shop,user).isPresent()) {
            throw new RuntimeException("이미 블랙리스트에 추가된 유저입니다."); // 이미 블랙리스트에 등록되었는지 검증
        }

        // 등록되지 않았을 경우
        BlackList blackList = BlackList.builder() // 블랙리스트 개체 생성
                .shop(shop)
                .user(user)
                .reason(request.getReason())
                .build();

        blackListRepository.save(blackList); // 블랙리스트 개체 저장
        
        return "성공적으로 블랙리스트에 추가되었습니다."; // 성공 구문 반환
    }

    /**
     * 사업자 블랙리스트 삭제
     * @param requests
     * @return
     */
    public String deleteBlackList(List<BlackListRequestDto> requests) {
        for (BlackListRequestDto request : requests) {
            // 가게 찾기
            Shop shop = shopRepository.findByEmail(request.getShopEmail())
                    .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다."));

            // 유저 찾기
            User user = userRepository.findByEmail(request.getUserEmail())
                    .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

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
     * @param request
     * @return
     */
    public List<ShopReservationResponseDto> getReservations(ShopReservationRequestDto request) {
        // 가게 찾기
        Shop shop = shopRepository.findByEmail(request.getShopEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다."));
        // MyBatis SqlMapper를 통해 예약 조회하기
        return reservationMapper.findAll(request);
    }

    /**
     * 사업자 예약 상세 조회
     * @param reservationId
     * @return
     */
    public ShopReservationDetailResponseDto getReservation(UUID reservationId) {
        // 예약 상세 조회 결과 반환
        return reservationRepository.findDetailById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("해당 예약을 찾을 수 없습니다."));
    }
}
