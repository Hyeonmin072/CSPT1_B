package com.myong.backend.service;

import com.myong.backend.domain.dto.coupon.CouponListResponseDto;
import com.myong.backend.domain.dto.coupon.CouponRegisterRequestDto;
import com.myong.backend.domain.dto.event.EventListResponseDto;
import com.myong.backend.domain.dto.event.EventRegisterRequestDto;
import com.myong.backend.domain.dto.shop.*;
import com.myong.backend.domain.entity.shop.Event;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.DiscountType;
import com.myong.backend.exception.ExistSameEmailException;
import com.myong.backend.exception.NotEqualVerifyCodeException;
import com.myong.backend.repository.CouponRepository;
import com.myong.backend.repository.EventRepository;
import com.myong.backend.repository.ShopRepository;
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
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
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


    /**
     * 사업자 회원가입 로직
     * @param request
     * @return
     */
    public String shopSignUp(ShopSignUpRequestDto request) {
            Shop shop = new Shop( // 가게 생성
                    request.getName(),
                    request.getAddress(),
                    request.getEmail(),
                    request.getTel(),
                    request.getBizId(),
                    request.getPassword(),
                    request.getPost()
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
        Optional<Shop> findShop = shopRepository.findByEmail(request.getEmail()); // 이메일로 가게 찾기
        if(!findShop.isPresent()){
            throw new NoSuchElementException("해당 가게를 찾지못했습니다");
        }

        Shop shop = findShop.get();
        return couponRepository.findByShop(shop.getId()); // // 가게의 고유 키를 통해 가져온 쿠폰 목록 반환
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
                request.getAmount(),
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
        Optional<Shop> findShop = shopRepository.findByEmail(request.getEmail()); // 이메일로 가게 찾기
        if(!findShop.isPresent()){
            throw new NoSuchElementException("해당 가게를 찾지못했습니다");
        }
        Shop shop = findShop.get();
        return eventRepository.findByShop(shop.getId());// 가게의 고유 키를 통해 가져온 이벤트 목록 반환
    }

    /**
     * 사업자 이벤트 등록 로직
     * @param request
     * @return
     */
    public String addEvent(EventRegisterRequestDto request) {
        Optional<Shop> findShop = shopRepository.findByEmail(request.getShopEmail());// 이메일로 가게 찾기
        if(!findShop.isPresent()){
            throw new NoSuchElementException("해당 가게를 찾지못했습니다.");
        }
        Shop shop = findShop.get();

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
}
