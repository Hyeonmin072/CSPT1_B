package com.myong.backend.service;

import com.myong.backend.domain.dto.review.ReviewRemoveRequestDto;
import com.myong.backend.domain.dto.shop.ShopRegisterReviewRequestDto;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.usershop.Review;
import com.myong.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


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

        System.out.println("리뷰 카운터"+shop.getReviewCount());
        //리뷰 등록시 해당 가게 평점 등록
        double shopRating = getReviewRating(shop.getTotalRating()+request.getReviewRating(),shop.getReviewCount()+1);
        shop.updateRating(shopRating,shop.getTotalRating()+request.getReviewRating(),shop.getReviewCount()+1);
        System.out.println("shopRating:"+shopRating+" totalRating+reviewRatring: "+shop.getTotalRating()+request.getReviewRating());
        shopRepository.save(shop);

        double desingerRating = getReviewRating(designer.getTotalRating()+request.getReviewRating(),designer.getReviewCount()+1);
        designer.updateRating(desingerRating,designer.getTotalRating()+request.getReviewRating(),designer.getReviewCount()+1);
        designerRepository.save(designer);

        return "리뷰가 성공적으로 등록되었습니다.";
    }

    public ResponseEntity<String> reviewRemove(ReviewRemoveRequestDto request){

        Review review = reviewRepository.findById(UUID.fromString(request.getReviewId())).orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        Shop shop = review.getShop();
        Designer designer = review.getDesigner();

        // 가게 평점 업데이트
        double shopRating = getReviewRating(shop.getTotalRating()-review.getRating(),shop.getReviewCount()-1);
        shop.updateRating(shopRating,shop.getTotalRating()-review.getRating(),shop.getReviewCount()-1);

        // 디자이너 평점 업데이트
        double designerRating = getReviewRating(designer.getTotalRating()-review.getRating(),designer.getReviewCount()-1);
        designer.updateRating(designerRating,designer.getTotalRating()-review.getRating(),designer.getReviewCount()-1);

        reviewRepository.deleteById(UUID.fromString(request.getReviewId()));

        return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
    }

    public double getReviewRating(double totalRating, int count){
        System.out.println(count);
        return totalRating/count;
    }


}
