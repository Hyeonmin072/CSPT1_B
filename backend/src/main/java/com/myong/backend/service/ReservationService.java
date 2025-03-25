package com.myong.backend.service;


import com.myong.backend.domain.dto.reservation.MenuListData;
import com.myong.backend.domain.dto.reservation.request.ReservationAcceptRequestDto;
import com.myong.backend.domain.dto.reservation.request.ReservationCreateRequestDto;
import com.myong.backend.domain.dto.reservation.response.*;
import com.myong.backend.domain.entity.business.PaymentMethod;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.business.ReservationStatus;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.DesignerHoliday;
import com.myong.backend.domain.entity.designer.DesignerRegularHoliday;
import com.myong.backend.domain.entity.shop.Menu;
import com.myong.backend.domain.entity.shop.MenuCategory;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.DiscountType;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final DesignerHolidayRepository designerHolidayRepository;


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

        int price =  menu.getPrice() - discountPrice(coupon.getPrice(),menu.getPrice(),coupon.getType());

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
    public List<ReservationPage1ResponseDto> loadSelectDesignerPage(String email){
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
    public ReservationPage2ResponseDto loadSelectTimePage(String email){

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
         Designer designer = designerRepository.findByEmail(designerEmail).orElseThrow(() -> new NoSuchElementException("해당 디자이너를 찾을 수 없습니다."));

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
        Designer designer = designerRepository.findByEmail(desingeremail).orElseThrow(() -> new NoSuchElementException("해당 디자이너를 찾을 수 없습니다."));


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
                        Menu.getName(),
                        Menu.getPrice(),
                        Menu.getEvent() != null ? discountPrice(Menu.getEvent().getPrice(),Menu.getPrice(),Menu.getEvent().getType()) : 0,
                        Menu.getEvent() != null ? Menu.getEvent().getPrice()+" "+( Menu.getEvent().getType() == DiscountType.PERCENT ? "%" : "원" )  : "",
                        Menu.getDesc(),
                        Menu.getImage()
                )
        ).collect(Collectors.toList());

        return new SelectMenuResponseDto(responseMenus,recommendMenus);

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
