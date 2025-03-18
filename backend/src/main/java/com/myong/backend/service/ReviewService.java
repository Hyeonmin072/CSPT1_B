package com.myong.backend.service;

import com.myong.backend.domain.dto.shop.ShopRegisterReviewRequestDto;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.usershop.Review;
import com.myong.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final DesignerRepository designerRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    public String registerReview(ShopRegisterReviewRequestDto request){

        Optional<Shop> findShop = shopRepository.findByEmail(request.getShopEmail());
        Optional<User> findUser = userRepository.findByEmail(request.getUserEmail());
        Optional<Designer> findDesigner = designerRepository.findByEmail(request.getDesignerEmail());
        Optional<Reservation> findReservation = reservationRepository.findById(request.getReservationId());

        if(!findShop.isPresent()){
            throw new NoSuchElementException("해당 가게를 찾지 못했습니다.");
        }
        if(!findUser.isPresent()){
            throw new NoSuchElementException("해당 유저를 찾지 못했습니다.");
        }
        if(!findDesigner.isPresent()){
            throw new NoSuchElementException("해당 디자이너를 찾지 못했습니다.");
        }
        if(!findReservation.isPresent()){
            throw new NoSuchElementException("해당 예약을 찾지 못했습니다.");
        }

        Shop shop = findShop.get();
        User user = findUser.get();
        Designer designer = findDesigner.get();
        Reservation reservation = findReservation.get();

        Review review = new Review(
                request.getReviewContent(),
                request.getReviewRating(),
                request.getReviewImg(),
                reservation,
                shop,
                designer,
                user
        );

        reviewRepository.save(review);

        //리뷰 등록시 해당 가게 평점 등록
        double shopRating = getReviewRating(shop.getTotalRating(),request.getReviewRating(),shop.getReviewCount());
        shop.updateRating(shopRating,request.getReviewRating());
        shopRepository.save(shop);

        double desingerRating = getReviewRating(designer.getTotalRating(),request.getReviewRating(),designer.getReviewCount());
        designer.updateRating(desingerRating,request.getReviewRating());
        designerRepository.save(designer);

        return "리뷰가 성공적으로 등록되었습니다.";
    }

    public double getReviewRating(double totalRating, double requestRating, int count){
        int reviewCount = count + 1;
        double rating = (totalRating+requestRating)/reviewCount;
        return rating;
    }
}
