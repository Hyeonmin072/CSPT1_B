package com.myong.backend.service;


import com.myong.backend.domain.dto.reservation.request.ReservationAcceptRequestDto;
import com.myong.backend.domain.dto.reservation.request.ReservationCreateRequestDto;
import com.myong.backend.domain.dto.reservation.response.ReservationInfoResponseDto;
import com.myong.backend.domain.dto.reservation.response.ReservationPage1ResponseDto;
import com.myong.backend.domain.dto.reservation.response.ReservationPage2ResponseDto;
import com.myong.backend.domain.entity.business.PaymentMethod;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.business.ReservationStatus;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.DesignerHoliday;
import com.myong.backend.domain.entity.designer.DesignerRegularHoliday;
import com.myong.backend.domain.entity.shop.Menu;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.DiscountType;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MenuRepository menuRepository;
    private final CouponRepository couponRepository;
    private final DesignerRepository designerRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final DesignerRegularHolidayRepository designerRegularHolidayRepository;
    private final DesignerHolidayRepository designerHolidayRepository


    //예약생성
    public ResponseEntity<String> createReservation(ReservationCreateRequestDto requestDto){

        Optional<User> ou = userRepository.findByEmail(requestDto.getUserEmail());
        if(!ou.isPresent()){
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
        }
        Optional<Shop> os = shopRepository.findByEmail(requestDto.getShopEmail());
        if(!os.isPresent()){
            throw new IllegalArgumentException("해당 샵이 존재하지 않습니다.");
        }
        Optional<Designer> od = designerRepository.findByEmail(requestDto.getDesignerEmail());
        if(!od.isPresent()){
            throw new IllegalArgumentException("해당 디자이너가 존재하지 않습니다.");
        }
        Optional<Menu> om = menuRepository.findById(UUID.fromString(requestDto.getMenuId()));
        if(!om.isPresent()){
            throw new IllegalArgumentException("해당 메뉴가 존재하지 않습니다.");
        }


        User user = ou.get();
        Designer designer = od.get();
        Shop shop = os.get();
        Menu menu = om.get();

        //쿠폰이 없을경우
        if(requestDto.getCouponId().equals("")){
            Reservation reservation = new Reservation(
                    requestDto.getServiceDate(),
                    requestDto.getPayMethod(),
                    menu.getPrice(),
                    menu,
                    shop,
                    designer,
                    user
            );

            reservationRepository.save(reservation);
            return ResponseEntity.ok("예약 등록이 완료되었습니다.");
        }

        // 쿠폰이 존재할 경우

        Optional<Coupon> oc = couponRepository.findById(UUID.fromString(requestDto.getCouponId()));
        if(!oc.isPresent()){
            throw new IllegalArgumentException("해당 쿠폰이 존재하지 않습니다.");
        }

        Coupon coupon = oc.get();

        int price = usingCoupon(coupon.getPrice(),menu.getPrice(),coupon.getType());

        Reservation reservation = new Reservation(
                requestDto.getServiceDate(),
                requestDto.getPayMethod(),
                price,
                menu,
                shop,
                designer,
                user,
                coupon
        );

        reservationRepository.save(reservation);
        return ResponseEntity.ok("예약 등록이 완료되었습니다.");
    }

    public ResponseEntity<String> acceptReservation(ReservationAcceptRequestDto requestDto){
        Optional<Reservation> or =  reservationRepository.findById(UUID.fromString(requestDto.getReservationId()));
        // 예약이 존재하지않으면 만료된예약
        if(!or.isPresent()){
            throw new IllegalArgumentException("만료된 예약입니다");
        }

        Reservation reservation = or.get();

        //이미 승낙된 예약이면 리턴
        if(reservation.getStatus() == ReservationStatus.SUCCESS){
            return ResponseEntity.ok("이미 승낙된 예약입니다.");
        }

        //예약승낙
        reservation.acceptReservation();
        reservationRepository.save(reservation);

        return ResponseEntity.ok("예약이 성공적으로 승낙되었습니다.");

    }

    public ResponseEntity<String> refuseReservation(ReservationAcceptRequestDto requestDto){
        Optional<Reservation> or =  reservationRepository.findById(UUID.fromString(requestDto.getReservationId()));
        if(!or.isPresent()){
            throw new IllegalArgumentException("이미 만료된 예약입니다.");
        }

        reservationRepository.deleteById(UUID.fromString(requestDto.getReservationId()));

        return ResponseEntity.ok("예약이 성공적으로 거절되었습니다.");
    }


    // 유저 예약 정보 조회
    public List<ReservationInfoResponseDto> getReservationByUser(String userEmail){

        Optional<User> ou = userRepository.findByEmail(userEmail);
        if(!ou.isPresent()){
            throw new IllegalArgumentException("존재하지않는 유저입니다.");
        }

        User user = ou.get();
        List<Reservation> reservationList =  reservationRepository.findAllByUser(user);

        return reservationList.stream()
                .map(reservation -> new ReservationInfoResponseDto(
                    reservation.getServiceDate(),
                    reservation.getMenu().getName(),
                    reservation.getShop().getName(),
                    reservation.getDesigner().getName(),
                    reservation.getPayMethod() == PaymentMethod.MEET ? "만나서 결제" : "카드결제",
                    reservation.getPrice(),
                    reservation.getStatus() == ReservationStatus.WAIT ? "수락대기" : "예약중"
                )).collect(Collectors.toList());

    }

    // 예약 페이지1
    public List<ReservationPage1ResponseDto> loadReservationPage1(String email){
        Shop shop = shopRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("해당 가게가 존재하지 않습니다."));

        List<Designer> desingers = shop.getDesigners();
        List<ReservationPage1ResponseDto> responseDtos =
                desingers.stream().map(designer -> new ReservationPage1ResponseDto(
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
    public ReservationPage2ResponseDto loadReservationPage2(String email){
        final int INTERVALMINUTES = 30;

        Designer designer = designerRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("해당 디자이너를 찾지 못했습니다."));


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

        // 예약 가능한 시간
        List<LocalTime> availableTimes = new ArrayList<>();
        LocalTime openTime = designer.getShop().getOpenTime();
        LocalTime closeTime = designer.getShop().getCloseTime();

        while(openTime.isBefore(closeTime)){
            reservationRepository.findByDe
        }







    }


    public int usingCoupon(int discount, int menuPrice, DiscountType discountType){
        if(discountType == DiscountType.FIXED){
            return menuPrice - discount;
        }
        return menuPrice - (int)(menuPrice * ((double)discount / 100));

    }



}
