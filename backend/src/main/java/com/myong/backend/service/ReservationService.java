package com.myong.backend.service;


import com.myong.backend.domain.dto.reservation.ReservationAcceptRequestDto;
import com.myong.backend.domain.dto.reservation.ReservationCreateRequestDto;
import com.myong.backend.domain.dto.reservation.ReservationInfoResponseDto;
import com.myong.backend.domain.entity.business.PaymentMethod;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.business.ReservationStatus;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Menu;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.DiscountType;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
                    reservation.getPayMethod(),
                    reservation.getPrice()
                )).collect(Collectors.toList());

    }


    public int usingCoupon(int discount, int menuPrice, DiscountType discountType){
        if(discountType == DiscountType.FIXED){
            return menuPrice - discount;
        }
        return menuPrice - (int)(menuPrice * ((double)discount / 100));

    }

}
