package com.myong.backend.service;


import com.myong.backend.domain.dto.reservation.ReservationRequestDto;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Menu;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MenuRepository menuRepository;
    private final CouponRepository couponRepository;
    private final DesignerRepository designerRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;


    public ResponseEntity<String> createReservation(ReservationRequestDto requestDto){

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

        Reservation reservation = new Reservation(
                requestDto.getServiceDate(),
                requestDto.getPayMethod(),
                menu,
                shop,
                designer,
                user,
                coupon
        );

        reservationRepository.save(reservation);
        return ResponseEntity.ok("예약 등록이 완료되었습니다.");
    }
}
